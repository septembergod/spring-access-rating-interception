package org.zspace.common.security.rating.config.record;

import org.zspace.common.security.rating.config.util.RateLimiter;

/**
 * 访问记录操作器
 */
public interface AccessRecord {

    /**
     * 对特定的key的RateLimiter进行保存
     */
    public RateLimiter save(String key, RateLimiter limiter);

    /**
     * 查询特定key的RateLimiter
     */
    public RateLimiter query(String key);

}