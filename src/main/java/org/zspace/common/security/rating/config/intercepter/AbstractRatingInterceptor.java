package org.zspace.common.security.rating.config.intercepter;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * 对方法拦截的抽象处理 -- 对前置 -- 处置之后的后置处理
 */
public abstract class AbstractRatingInterceptor implements InitializingBean{

    protected final Log logger = LogFactory.getLog(getClass());

    private AccessDecisionManager accessDecisionManager;

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.accessDecisionManager, "An AccessDecisionManager is required");
        Assert.notNull(this.obtainSecurityMetadataSource(), "An SecurityMetadataSource is required");
    }

    protected InterceptorStatusToken beforeInvocation(Object object) {
        Assert.notNull(object, "Object was null");
        final boolean debug = logger.isDebugEnabled();
        if (!getSecureObjectClass().isAssignableFrom(object.getClass())) {
            throw new IllegalArgumentException(
                    "Security invocation attempted for object "
                            + object.getClass().getName()
                            + " but AbstractSecurityInterceptor only configured to support secure objects of type: "
                            + getSecureObjectClass());
        }
        Collection<ConfigAttribute> attributes = this.obtainSecurityMetadataSource()
                .getAttributes(object);
        if (attributes == null || attributes.isEmpty()) {
            return null; // no further work post-invocation
        }
        // Attempt authorization
        try {
            this.accessDecisionManager.decide(null, object, attributes);
        }
        catch (AccessDeniedException accessDeniedException) {
            throw accessDeniedException;
        }
        return new InterceptorStatusToken(null, false, attributes, object);
    }

    public AccessDecisionManager getAccessDecisionManager() {
        return accessDecisionManager;
    }
    public abstract Class<?> getSecureObjectClass();
    public abstract SecurityMetadataSource obtainSecurityMetadataSource();

    public void setAccessDecisionManager(AccessDecisionManager accessDecisionManager) {
        this.accessDecisionManager = accessDecisionManager;
    }

}
