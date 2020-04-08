package org.zspace.common.security.rating.config.annotation.configuration;

import org.springframework.context.annotation.AutoProxyRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * 对于方法访问控制需要的组件导入
 */
public class GlobalMethodRatingSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        String autoProxyClassName = AutoProxyRegistrar.class.getName();
        List<String> classNames = new ArrayList<>(4);
        classNames.add(MethodRatingMetadataSourceAdvisorRegistrar.class.getName());
        classNames.add(autoProxyClassName);
        classNames.add(GlobalMethodRatingConfiguration.class.getName());
        return classNames.toArray(new String[0]);
    }
}