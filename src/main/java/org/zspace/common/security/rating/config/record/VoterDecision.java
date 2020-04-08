package org.zspace.common.security.rating.config.record;

import org.springframework.security.access.ConfigAttribute;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @param <T>
 */
public interface VoterDecision<T> {

    /**
     * 对于特定的对象进行 投票判断
     * @param obj
     * @return
     */
    public boolean decision (T obj, ConfigAttribute attribute);

    /**
     * 表示是方法拦截，还是web请求拦截
     * @return
     */
    public boolean web();

}