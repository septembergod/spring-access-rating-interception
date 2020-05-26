package org.zspace.common.security.rating.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by September on 2019/11/12 18:07
 */
public class ContextUtil {

    /**
     * 获取request对象
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取Response对象
     */
    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    /**
     * 获取Session对象
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }


    public static void setSessionData(String key, Object value) {
        try {
            getSession().setAttribute(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final static String findString(String name) {
        return getRequest().getParameter(name);
    }

    public static <T> T getSessionData(String key) {
        return (T) getSession().getAttribute(key);
    }

    /**
     * 获取请求的协议是http的还是https的
     */
    public static String getScheme() {
        String scheme = "https";
        try {
            scheme = getRequest().getScheme();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scheme;
    }

    //    /**
//     * 如果需要使用 useragent信息
//     * 可引用下包
//     *
//     * <dependency>
//     * <groupId>eu.bitwalker</groupId>
//     * <artifactId>UserAgentUtils</artifactId>
//     * <version>1.21</version>
//     * </dependency>
//     *
//     * @return
//     */
    /*public static boolean isPC() {
        try {
            return new UserAgent(getRequest().getHeader("User-Agent")).getOperatingSystem().getDeviceType() == DeviceType.COMPUTER;
        } catch (Exception e) {
        }
        return false;
    }*/
    public static String getIP() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        try {

            // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
            String ip = request.getHeader("x-real-ip");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Forwarded-For");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("Proxy-Client-IP");
                }
                if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("WL-Proxy-Client-IP");
                }
                if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("HTTP_CLIENT_IP");
                }
                if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                }
                if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
            } else if (ip.length() > 15) {
                String[] ips = ip.split(",");
                for (int index = 0; index < ips.length; index++) {
                    String strIp = ips[index];
                    if (!("unknown".equalsIgnoreCase(strIp))) {
                        ip = strIp;
                        break;
                    }
                }
            }
            return ip;
        } catch (Exception e) {
        }
        return request.getRemoteAddr();
    }

    public static String getCookieValue(String cookieName) {
        return getCookieValue(getRequest(), cookieName);
    }

    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        if (!StringUtils.isBlank(cookieName)) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(cookieName)) {
                        return cookie.getValue();
                    }
                }
            }
        }
        return null;
    }

    public static RequestAttributes getAttr() {
        return RequestContextHolder.getRequestAttributes();
    }

    public static void setAttr(RequestAttributes attr) {
        RequestContextHolder.setRequestAttributes(attr);
    }

}
