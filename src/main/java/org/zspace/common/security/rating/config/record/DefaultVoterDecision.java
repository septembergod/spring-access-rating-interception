package org.zspace.common.security.rating.config.record;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.util.ObjectUtils;
import org.zspace.common.security.rating.config.util.RateLimiter;
import org.zspace.common.security.rating.util.ContextUtil;

import java.lang.reflect.Method;

public class DefaultVoterDecision implements VoterDecision<Object> {

    private AccessRecord accessRecord = new DefaultAccessRecord();

    private Object mutex = new Object();

    @Override
    public boolean decision(Object obj, ConfigAttribute attribute) {
        /**
         * 此处对请求是否能访问具体判断
         */
        if (obj instanceof MethodInvocation) {
            MethodInvocation mi = (MethodInvocation) obj;
            String key = key(mi);
            RateLimiter limiter = accessRecord.query(key);
            if (ObjectUtils.isEmpty(limiter)) {
                synchronized (this.mutex) {
                    limiter = accessRecord.query(key);
                    if (ObjectUtils.isEmpty(limiter)) {
                        String attr = attribute.getAttribute();
                        int value = Integer.parseInt(attr);
                        accessRecord.save(key, RateLimiter.create(value));
                    }
                }
            }
            limiter = accessRecord.query(key);
            /**
             * 返回的等待时间
             */
            double waite = limiter.acquire();
            if (waite > 0) {
                return false;
            }
            return true;
        }
        return true;
    }

    @Override
    public boolean web() {
        return true;
    }

    private String key(MethodInvocation mi) {
        String key = "";
        String ip = ContextUtil.getIP();
        key = key + ip;
        Method method = mi.getMethod();
        key = key + ":" + method.getDeclaringClass().getName();
        key = key + ":" + method.getName();
        Class<?>[] clss = method.getParameterTypes();
        if (clss != null) {
            for (int i = 0; i < clss.length; i++) {
                Class<?> cls = clss[i];
                key = key + ":" + cls.getSimpleName();
            }
        }
        return key;
    }
}
