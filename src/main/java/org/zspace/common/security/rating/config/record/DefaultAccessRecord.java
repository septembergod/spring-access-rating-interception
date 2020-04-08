package org.zspace.common.security.rating.config.record;

import com.google.common.collect.Maps;
import org.springframework.util.ObjectUtils;
import org.zspace.common.security.rating.config.util.RateLimiter;

import java.util.Map;

/**
 * 本地记录访问记录 -- 保存key 与 RateLimiter的映射关系
 */
public class DefaultAccessRecord implements AccessRecord{

    /**
     * 对url的访问进行控制  key=ip:methodName:parameterTypeName... value:记录NoSleepRateLimiter  :没有可用令牌直接返回
     */
    public static final Map<String, RateLimiter> limiterMap = Maps.newHashMap();
    @Override
    public RateLimiter save(String key, RateLimiter limiter) {

        /**
         * 需要考虑并发安全， 此处使用调用者实现了单台服务的并发控制
         */
        RateLimiter oldLimter = limiterMap.get(key);
        if (ObjectUtils.isEmpty(oldLimter)){
            limiterMap.put(key, limiter);
            oldLimter = limiter;
        }
        return oldLimter;
    }
    @Override
    public RateLimiter query(String key) {
        return limiterMap.get(key);
    }
}