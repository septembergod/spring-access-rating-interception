package org.zspace.common.security.rating.config.annotation.configuration;

import org.aopalliance.intercept.MethodInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.method.DelegatingMethodSecurityMetadataSource;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.zspace.common.security.rating.config.access.AffirmativeBased;
import org.zspace.common.security.rating.config.access.MethodRatingMetadataSource;
import org.zspace.common.security.rating.config.access.RatingVoter;
import org.zspace.common.security.rating.config.intercepter.MethodRatingInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * 对方法频率访问安全导入组件
 * author:liuwenqing
 */
@Configuration
public class GlobalMethodRatingConfiguration implements ImportAware, SmartInitializingSingleton, BeanFactoryAware {

    private static final Log logger = LogFactory
            .getLog(GlobalMethodRatingConfiguration.class);

    private BeanFactory context;
    private MethodRatingInterceptor methodRatingInterceptor;

    @Bean
    public MethodInterceptor methodRatingInterceptor() throws Exception {
        this.methodRatingInterceptor = new MethodRatingInterceptor();
        methodRatingInterceptor.setAccessDecisionManager(accessDecisionManager());
        methodRatingInterceptor
                .setSecurityMetadataSource(methodRatingMetadataSource());
        return this.methodRatingInterceptor;
    }

    /**
     * 注入访问角色器
     */
    @Bean
    protected AccessDecisionManager accessDecisionManager() {
        List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<AccessDecisionVoter<? extends Object>>();
        decisionVoters.add(new RatingVoter());
        return new AffirmativeBased(decisionVoters);
    }

    /**
     * 注入方法注解数据获取
     * @return
     */
    @Bean
    public MethodSecurityMetadataSource methodRatingMetadataSource() {
        List<MethodSecurityMetadataSource> sources = new ArrayList<>();
        sources.add(new MethodRatingMetadataSource());
        return new DelegatingMethodSecurityMetadataSource(sources);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.context = beanFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {

    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        //对于注解信息获取
    }
}