package org.zspace.common.security.rating.config.annotation.configuration;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 对方法访问 - 注册配置类
 */
public class MethodRatingMetadataSourceAdvisorRegistrar implements
        ImportBeanDefinitionRegistrar {

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {

        BeanDefinitionBuilder advisor = BeanDefinitionBuilder
                .rootBeanDefinition(MethodRatingMetadataSourceAdvisor.class);
        advisor.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        advisor.addConstructorArgValue("methodRatingInterceptor");
        advisor.addConstructorArgReference("methodRatingMetadataSource");
        advisor.addConstructorArgValue("methodRatingMetadataSource");
        registry.registerBeanDefinition("methodRatingDataSourceAdvisor",
                advisor.getBeanDefinition());
    }
}