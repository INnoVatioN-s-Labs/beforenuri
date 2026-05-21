package com.toyproject.t4lk.session;

import jakarta.servlet.http.HttpServletRequest;

public final class ClientIpResolver {

    private static final String[] HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "X-Real-IP",
            "CF-Connecting-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP"
    };

    private ClientIpResolver() {
    }

    public static String resolveClientIp(HttpServletRequest request) {
        for (String header : HEADER_CANDIDATES) {
            String value = request.getHeader(header);
            if (value != null && !value.isBlank() && !"unknown".equalsIgnoreCase(value)) {
                return value.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

    public static String toDisplaySuffix(String ip) {
        if (ip == null || ip.isBlank()) {
            return "0.0";
        }
        String normalized = ip;
        int zoneIndex = normalized.indexOf('%');
        if (zoneIndex >= 0) {
            normalized = normalized.substring(0, zoneIndex);
        }
        if (normalized.contains(":")) {
            String[] segments = normalized.split(":");
            String first = segments.length > 0 ? segments[0] : "";
            String second = segments.length > 1 ? segments[1] : "";
            if (first.isBlank() && second.isBlank()) {
                return "::";
            }
            return (first.isBlank() ? "0" : first) + "." + (second.isBlank() ? "0" : second);
        }
        String[] octets = normalized.split("\\.");
        if (octets.length < 2) {
            return normalized;
        }
        return octets[0] + "." + octets[1];
    }
}
