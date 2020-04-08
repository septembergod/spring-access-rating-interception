package org.zspace.common.security.rating.web.comfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zspace.common.security.rating.web.filter.SpringRatingFilter;

/**
 * 在spring boot环境中 直接使用 不用手动注册filter
 *     在：非spring 环境中需要手动设置 SpringRatingFilter
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SpringRatingAutoConfiguratino {
    private static final String DEFAULT_FILTER_NAME = "springRatingFilter";
    @Bean
    public FilterRegistrationBean securityFilterChainRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new SpringRatingFilter());
        registration.setName(DEFAULT_FILTER_NAME);
        return registration;
    }

}


