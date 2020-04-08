package org.zspace.common.security.rating.config.annotation.configuration;

import java.lang.annotation.*;

/**
 * 方法注解，注解方法支持的访问频率
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.METHOD })
@Documented
public @interface Rating {

    public long value() default 5;

}
