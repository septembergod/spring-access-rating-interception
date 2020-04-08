package org.zspace.common.security.rating.util;

import javax.servlet.http.HttpServletRequest;

public class KeyUtils {
    private static String ip(HttpServletRequest request) {
        String ip = WebUtils.getIpAddress(request);
        return ip;
    }
}