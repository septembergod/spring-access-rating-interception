package org.zspace.common.security.rating.config.intercepter;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.zspace.common.security.rating.util.ContextUtil;
import org.zspace.common.security.rating.util.HttpResponseUtil;

/**
 * 具体的方法拦截逻辑 -- 对执行进行拦截处理
 */
public class MethodRatingInterceptor extends AbstractRatingInterceptor implements MethodInterceptor {

    private MethodSecurityMetadataSource securityMetadataSource;

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        try {
            InterceptorStatusToken token = super.beforeInvocation(mi);
        } catch (AccessDeniedException accessDeniedException) {
            /**
             * 如果拒绝访问，表示此事访问超过访问限制
             */

            HttpResponseUtil.writeToResponse(ContextUtil.getResponse(), HttpResponseUtil.frequentlyResponse());
            return null;
        }
        Object result = mi.proceed();
        return result;
    }

    @Override
    public Class<?> getSecureObjectClass() {
        return MethodInvocation.class;
    }

    public MethodSecurityMetadataSource getSecurityMetadataSource() {
        return this.securityMetadataSource;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return this.securityMetadataSource;
    }

    public void setSecurityMetadataSource(MethodSecurityMetadataSource newSource) {
        this.securityMetadataSource = newSource;
    }

}
