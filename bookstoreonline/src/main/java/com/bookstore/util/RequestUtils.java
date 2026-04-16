package com.bookstore.util;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtils {
    
    public static String getClientIp(HttpServletRequest request) {
        String remoteAddr = "";
        
        if (request != null) {
            remoteAddr = request.getHeader("X-Forwarded-For");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getHeader("X-Real-IP");
            }
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        
        // If there are multiple IPs in X-Forwarded-For, take the first one
        if (remoteAddr != null && remoteAddr.contains(",")) {
            remoteAddr = remoteAddr.split(",")[0].trim();
        }
        
        return remoteAddr != null ? remoteAddr : "127.0.0.1";
    }
}
