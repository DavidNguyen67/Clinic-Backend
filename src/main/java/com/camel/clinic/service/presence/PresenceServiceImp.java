package com.camel.clinic.service.presence;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
public class PresenceServiceImp implements PresenceService {

    private static final String KEY_PREFIX = "presence:";
    private static final String ONLINE_SET = "online_users";
    private static final Duration TTL = Duration.ofSeconds(60);

    private final StringRedisTemplate redis;

    @Override
    public void setOnline(String userId) {
        redis.opsForValue().set(KEY_PREFIX + userId, "online", TTL);
        redis.opsForSet().add(ONLINE_SET, userId);
    }

    @Override
    public void setOffline(String userId) {
        redis.delete(KEY_PREFIX + userId);
        redis.opsForSet().remove(ONLINE_SET, userId);
    }

    @Override
    public boolean isOnline(String userId) {
        return redis.hasKey(KEY_PREFIX + userId);
    }

    @Override
    public Map<String, Boolean> getOnlineUsers(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) return Map.of();

        List<Object> results = redis.executePipelined((RedisCallback<?>) connection -> {
            for (String userId : userIds) {
                connection.keyCommands().exists((KEY_PREFIX + userId).getBytes());
            }
            return null;
        });

        Map<String, Boolean> onlineMap = new LinkedHashMap<>();
        for (int i = 0; i < userIds.size(); i++) {
            Boolean exists = (Boolean) results.get(i);
            onlineMap.put(userIds.get(i), Boolean.TRUE.equals(exists));
        }
        return onlineMap;
    }

    public Set<String> getAllOnlineUsers() {
        return redis.opsForSet().members(ONLINE_SET);
    }

    public void heartbeat(String userId) {
        Boolean isMember = redis.opsForSet().isMember(ONLINE_SET, userId);
        if (!Boolean.TRUE.equals(isMember)) {
            redis.opsForSet().add(ONLINE_SET, userId);
        }
        redis.expire(KEY_PREFIX + userId, TTL);
    }
}