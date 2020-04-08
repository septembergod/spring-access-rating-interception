package org.zspace.common.security.rating.config.access;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;

/**
 * 基于肯定方式的投票器
 *   注：当一个投票器肯定 就表示可以成功访问
 */

public class AffirmativeBased implements AccessDecisionManager, InitializingBean {

    protected final Log logger = LogFactory.getLog(getClass());

    private List<AccessDecisionVoter<? extends Object>> decisionVoters;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notEmpty(this.decisionVoters, "A list of AccessDecisionVoters is required");
    }

    public AffirmativeBased(List<AccessDecisionVoter<? extends Object>> decisionVoters) {
        this.decisionVoters = decisionVoters;
    }

    public List<AccessDecisionVoter<? extends Object>> getDecisionVoters() {
        return this.decisionVoters;
    }

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        int deny = 0;
        for (AccessDecisionVoter voter : getDecisionVoters()) {
            int result = voter.vote(authentication, object, configAttributes);
            if (logger.isDebugEnabled()) {
                logger.debug("Voter: " + voter + ", returned: " + result);
            }
            switch (result) {
                case AccessDecisionVoter.ACCESS_GRANTED:
                    return;
                case AccessDecisionVoter.ACCESS_DENIED:
                    deny++;
                    break;
                default:
                    break;
            }
        }
        if (deny > 0) {
            throw new AccessDeniedException("Access is denied");
        }
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        for (AccessDecisionVoter voter : this.decisionVoters) {
            if (voter.supports(attribute)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        for (AccessDecisionVoter voter : this.decisionVoters) {
            if (!voter.supports(clazz)) {
                return false;
            }
        }
        return true;
    }


}