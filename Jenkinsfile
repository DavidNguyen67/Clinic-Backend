pipeline {
    agent any

    environment {
        DOCKERHUB_REPO     = 'davidnguyendev/be-clinic'
        APP_CONTAINER_NAME = 'be-clinic'
        APP_PORT           = '8080'
        KEEP_IMAGES        = '3'

        DOCKERHUB_CREDS    = 'dockerhub-credentials'
        SSH_CREDS          = 'vps-ssh-credentials'
        TELEGRAM_CREDS     = 'telegram-bot-token'
        TELEGRAM_CHAT_ID   = 'telegram-chat-id'

        VPS_HOST           = 'vps.ip.address'
        VPS_USER           = 'root'

        GIT_COMMIT_SHORT   = ''
        IMAGE_TAG          = ''
    }

    triggers {
        githubPush()
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {

        stage('🔍 Checkout') {
            when {
               expression {
                       return env.GIT_BRANCH == 'master' || env.GIT_BRANCH == 'origin/master'
               }
            }
            steps {
                checkout scm
                script {
                    GIT_COMMIT_SHORT = sh(
                        script: "git rev-parse --short HEAD",
                        returnStdout: true
                    ).trim()
                    IMAGE_TAG = "${DOCKERHUB_REPO}:${GIT_COMMIT_SHORT}"
                    env.GIT_COMMIT_SHORT = GIT_COMMIT_SHORT
                    env.IMAGE_TAG        = IMAGE_TAG
                    echo "📦 Image sẽ được tag: ${IMAGE_TAG}"
                }
            }
        }

        stage('🏗️ Build Docker Image') {
            when {
               expression {
                       return env.GIT_BRANCH == 'master' || env.GIT_BRANCH == 'origin/master'
               }
            }
            steps {
                script {
                    echo "🔨 Building image: ${IMAGE_TAG}"
                    sh "docker build -t ${IMAGE_TAG} ."
                    sh "docker tag ${IMAGE_TAG} ${DOCKERHUB_REPO}:latest"
                }
            }
        }

        stage('🚀 Push to DockerHub') {
            when {
               expression {
                       return env.GIT_BRANCH == 'master' || env.GIT_BRANCH == 'origin/master'
               }
            }
            steps {
                withCredentials([usernamePassword(
                    credentialsId: "${DOCKERHUB_CREDS}",
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push ''' + IMAGE_TAG + '''
                        docker push ''' + DOCKERHUB_REPO + ''':latest
                        docker logout
                    '''
                }
            }
        }

        stage('🌐 Deploy to VPS') {
            when {
               expression {
                       return env.GIT_BRANCH == 'master' || env.GIT_BRANCH == 'origin/master'
               }
            }
            steps {
                withCredentials([
                    sshUserPrivateKey(
                        credentialsId: "${SSH_CREDS}",
                        keyFileVariable: 'SSH_KEY'
                    ),
                    usernamePassword(
                        credentialsId: "${DOCKERHUB_CREDS}",
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    script {
                        def deployScript = """
                            set -e

                            echo "=== [1/5] Login DockerHub ==="
                            echo "${env.DOCKER_PASS}" | docker login -u "${env.DOCKER_USER}" --password-stdin

                            echo "=== [2/5] Pull image mới: ${env.IMAGE_TAG} ==="
                            docker pull ${env.IMAGE_TAG}

                            echo "=== [3/5] Dừng & xóa container cũ (nếu có) ==="
                            docker stop ${APP_CONTAINER_NAME} 2>/dev/null || true
                            docker rm   ${APP_CONTAINER_NAME} 2>/dev/null || true

                            echo "=== [4/5] Chạy container mới ==="
                            docker run -d \\
                                --name ${APP_CONTAINER_NAME} \\
                                --restart unless-stopped \\
                                -p ${APP_PORT}:8080 \\
                                ${env.IMAGE_TAG}

                            echo "=== [5/5] Dọn image cũ — giữ lại ${KEEP_IMAGES} gần nhất ==="
                            docker images ${DOCKERHUB_REPO} --format '{{.Tag}} {{.ID}}' \\
                                | grep -v latest \\
                                | sort -r \\
                                | tail -n +\$((${KEEP_IMAGES} + 1)) \\
                                | awk '{print \$2}' \\
                                | xargs -r docker rmi -f || true

                            docker logout
                            echo "✅ Deploy thành công!"
                        """

                        sh """
                            ssh -i \$SSH_KEY \
                                -o StrictHostKeyChecking=no \
                                -o ConnectTimeout=10 \
                                ${VPS_USER}@${VPS_HOST} '${deployScript}'
                        """
                    }
                }
            }
        }
    }

    // POST — Telegram notification
    post {
        success {
            script {
                sendTelegram("✅ *BUILD THÀNH CÔNG*\n" +
                    "📦 *Project:* `${env.JOB_NAME}`\n" +
                    "🔖 *Image:* `${env.IMAGE_TAG}`\n" +
                    "🔢 *Build:* [#${env.BUILD_NUMBER}](${env.BUILD_URL})\n" +
                    "🌿 *Branch:* `${env.GIT_BRANCH}`\n" +
                    "⏱️ *Thời gian:* ${currentBuild.durationString}")
            }
        }

        failure {
            script {
                sendTelegram("❌ *BUILD THẤT BẠI*\n" +
                    "📦 *Project:* `${env.JOB_NAME}`\n" +
                    "🔢 *Build:* [#${env.BUILD_NUMBER}](${env.BUILD_URL})\n" +
                    "🌿 *Branch:* `${env.GIT_BRANCH}`\n" +
                    "⏱️ *Thời gian:* ${currentBuild.durationString}\n" +
                    "👉 Log đính kèm bên dưới ↓")

                sendLogFile("failure")
            }
        }

        aborted {
            script {
                sendTelegram("⚠️ *BUILD BỊ HỦY*\n" +
                    "📦 *Project:* `${env.JOB_NAME}`\n" +
                    "🔢 *Build:* [#${env.BUILD_NUMBER}](${env.BUILD_URL})\n" +
                    "🌿 *Branch:* `${env.GIT_BRANCH}`\n" +
                    "⏱️ *Thời gian:* ${currentBuild.durationString}\n" +
                    "👉 Log đính kèm bên dưới ↓")

                sendLogFile("aborted")
            }
        }

        always {
            script {
                sh "docker rmi ${env.IMAGE_TAG} ${DOCKERHUB_REPO}:latest 2>/dev/null || true"
            }
        }
    }
}

// ─────────────────────────────────────────────
// Helper: gửi message text qua Telegram
// ─────────────────────────────────────────────
def sendTelegram(String message) {
    withCredentials([
        string(credentialsId: "${TELEGRAM_CREDS}",   variable: 'BOT_TOKEN'),
        string(credentialsId: "${TELEGRAM_CHAT_ID}", variable: 'CHAT_ID')
    ]) {
        sh """
            curl -s -X POST "https://api.telegram.org/bot\${BOT_TOKEN}/sendMessage" \\
                -d chat_id="\${CHAT_ID}" \\
                -d parse_mode="Markdown" \\
                -d disable_web_page_preview="true" \\
                -d text="${message.replace('"', '\\"')}"
        """
    }
}

// ─────────────────────────────────────────────
// Helper: xuất log build → gửi file .log lên Telegram
//
// Cách hoạt động:
//   1. Dùng Jenkins REST API (build-in) để tải raw console log
//      về file tạm /tmp/jenkins-build-<job>-<number>.log
//   2. Gửi file đó qua Telegram sendDocument API
//   3. Xóa file tạm sau khi gửi xong
//
// Lưu ý: Jenkins agent cần có curl và quyền gọi
//   localhost:<JENKINS_PORT>/job/.../consoleText
// ─────────────────────────────────────────────
def sendLogFile(String status) {
    withCredentials([
        string(credentialsId: "${TELEGRAM_CREDS}",   variable: 'BOT_TOKEN'),
        string(credentialsId: "${TELEGRAM_CHAT_ID}", variable: 'CHAT_ID')
    ]) {
        script {
            // Tên file gửi kèm — dễ nhận biết trong Telegram
            def safeJobName  = env.JOB_NAME.replaceAll('[^a-zA-Z0-9_-]', '_')
            def logFileName  = "${safeJobName}_build-${env.BUILD_NUMBER}_${status}.log"
            def logFilePath  = "/tmp/${logFileName}"

            // Caption hiển thị dưới file trong Telegram
            def caption = "📋 Build log — ${env.JOB_NAME} #${env.BUILD_NUMBER} [${status.toUpperCase()}]"

            sh """
                # ── 1. Tải console log từ Jenkins API ──────────────────────────────
                # JENKINS_URL được Jenkins inject sẵn vào mọi build.
                # Nếu agent và master cùng máy thì dùng localhost cho nhanh.
                curl -s --max-time 30 \\
                    "\${JENKINS_URL}job/${env.JOB_NAME}/${env.BUILD_NUMBER}/consoleText" \\
                    -o "${logFilePath}" || true

                # Fallback: nếu URL trên không lấy được (multi-branch, folder job…)
                # thì thử lấy trực tiếp qua biến BUILD_URL Jenkins cũng inject sẵn.
                if [ ! -s "${logFilePath}" ]; then
                    curl -s --max-time 30 \\
                        "\${BUILD_URL}consoleText" \\
                        -o "${logFilePath}" || true
                fi

                # Nếu vẫn trống → tạo file thông báo lỗi để không gửi file rỗng
                if [ ! -s "${logFilePath}" ]; then
                    echo "Không lấy được console log từ Jenkins API." > "${logFilePath}"
                fi

                # ── 2. Gửi file lên Telegram ────────────────────────────────────────
                curl -s -X POST "https://api.telegram.org/bot\${BOT_TOKEN}/sendDocument" \\
                    -F chat_id="\${CHAT_ID}" \\
                    -F caption="${caption}" \\
                    -F document=@"${logFilePath}"

                # ── 3. Dọn file tạm ─────────────────────────────────────────────────
                rm -f "${logFilePath}"
            """
        }
    }
}