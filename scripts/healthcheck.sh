#!/bin/bash
set -euo pipefail

NAME="__APP_NAME__"
PORT="__APP_PORT__"
REPO="__DOCKERHUB_REPO__"
CURRENT_TAG="__IMAGE_TAG__"
APP_DIR="__APP_DIR__"             # inject từ Jenkinsfile, không hardcode

HEALTH_URL="http://localhost:${PORT}/actuator/health"
MAX_RETRY=18
RETRY_INTERVAL=5

# ── File tạm có namespace theo build, tránh conflict BE/FE chạy song song ──
HC_BODY="/tmp/hc_body___BUILD_TAG__.json"

# ── Cleanup file tạm khi exit/abort ──────────────────────────
trap 'rm -f "${BASH_SOURCE[0]}" "${HC_BODY}"' EXIT INT TERM

rollback() {
    echo "🔄 Bắt đầu rollback..."

    PREV_IMAGE=$(docker images "${REPO}" --format '{{.Repository}}:{{.Tag}} {{.CreatedAt}}' \
        | grep -v latest \
        | grep -v "${CURRENT_TAG}" \
        | sort -t' ' -k2,3 -r \
        | head -1 \
        | awk '{print $1}')

    if [ -z "${PREV_IMAGE}" ]; then
        echo "❌ Không tìm thấy image cũ để rollback!"
        exit 1
    fi

    echo "🔄 Rollback về: ${PREV_IMAGE}"
    docker rm -f "${NAME}" 2>/dev/null || true
    docker run -d \
        --name "${NAME}" \
        --restart unless-stopped \
        --env-file "${APP_DIR}/.env" \
        -p "${PORT}:8080" \
        "${PREV_IMAGE}"

    echo "⏳ Chờ rollback container khởi động (30s)..."
    sleep 30

    if curl -sf "${HEALTH_URL}" | grep -q '"status":"UP"'; then
        echo "✅ Rollback thành công — đang chạy ${PREV_IMAGE}"
    else
        echo "❌ Rollback cũng không healthy! Cần can thiệp thủ công."
    fi

    exit 1
}

echo "=== [Health Check] Đợi ${NAME} healthy tại ${HEALTH_URL} ==="
echo "    Timeout: $((MAX_RETRY * RETRY_INTERVAL))s | Interval: ${RETRY_INTERVAL}s"

for i in $(seq 1 "${MAX_RETRY}"); do
    HTTP_STATUS=$(curl -s -o "${HC_BODY}" -w "%{http_code}" "${HEALTH_URL}" || echo "000")

    if [ "${HTTP_STATUS}" = "200" ]; then
        APP_STATUS=$(grep -o '"status":"[^"]*"' "${HC_BODY}" | head -1 | cut -d'"' -f4 || echo "")
        if [ "${APP_STATUS}" = "UP" ]; then
            echo ""
            echo "✅ App HEALTHY sau $((i * RETRY_INTERVAL))s — status: UP"
            exit 0
        else
            echo "⚠️  HTTP 200 nhưng status=${APP_STATUS} (attempt ${i}/${MAX_RETRY})"
        fi
    else
        echo "⏳ Attempt ${i}/${MAX_RETRY} — HTTP ${HTTP_STATUS} — chờ ${RETRY_INTERVAL}s..."
    fi

    if (( i % 5 == 0 )); then
        echo "--- Container logs (tail 20) ---"
        docker logs --tail=20 "${NAME}" 2>&1 || true
        echo "--------------------------------"
    fi

    sleep "${RETRY_INTERVAL}"
done

echo ""
echo "❌ Health check THẤT BẠI sau $((MAX_RETRY * RETRY_INTERVAL))s!"
echo "--- Container logs (tail 50) ---"
docker logs --tail=50 "${NAME}" 2>&1 || true
echo "--- Docker inspect state ---"
docker inspect "${NAME}" --format='{{json .State}}' 2>/dev/null || true

rollback