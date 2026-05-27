package com.camel.clinic.service.presence;

import java.util.List;
import java.util.Map;

public interface PresenceService {
    void setOnline(String userId);

    void setOffline(String userId);

    boolean isOnline(String userId);

    Map<String, Boolean> getOnlineUsers(List<String> userIds);
}