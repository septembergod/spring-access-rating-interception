package org.zspace.common.security.rating.config;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.zspace.common.security.rating.config.annotation.configuration.GlobalMethodRatingSelector;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 启动对方法访问评率的拦截
 * author:liuwenqing
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = { java.lang.annotation.ElementType.TYPE })
@Documented
@Import({GlobalMethodRatingSelector.class})
@Configuration
public @interface EnableGlobalMethodRating {

    boolean proxyTargetClass() default false;

    AdviceMode mode() default AdviceMode.PROXY;

    int order() default Ordered.LOWEST_PRECEDENCE;

}
