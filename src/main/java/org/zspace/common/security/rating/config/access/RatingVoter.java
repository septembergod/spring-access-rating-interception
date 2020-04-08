package org.zspace.common.security.rating.config.access;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.zspace.common.security.rating.config.record.DefaultVoterDecision;
import org.zspace.common.security.rating.config.record.VoterDecision;

import java.util.Collection;

/**
 * 访问评率投票器
 *   对当前方法访问次数 是否已经超越限制进行投票
 */

public class RatingVoter implements AccessDecisionVoter<Object> {

    private VoterDecision voterDecision = new DefaultVoterDecision();

    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    public boolean supports(Class<?> clazz) {
        return true;
    }

    /**
     * 对方法是否可以访问进行投票
     */
    public int vote(Authentication authentication, Object object,
                    Collection<ConfigAttribute> attributes) {

        int result = ACCESS_ABSTAIN;

        for (ConfigAttribute attribute : attributes) {
            if (this.supports(attribute)) {
                result = ACCESS_DENIED;
                /**
                 * 此处需要根据方法的属性，来对访问记录的方法参数判断是否已超越访问次数
                 */
                if(voterDecision.decision(object, attribute)){
                    return ACCESS_GRANTED;
                }
            }
        }

        return result;
    }

}
