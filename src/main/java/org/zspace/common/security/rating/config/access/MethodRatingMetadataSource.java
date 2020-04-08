package org.zspace.common.security.rating.config.access;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.annotation.Jsr250SecurityConfig;
import org.springframework.security.access.method.AbstractFallbackMethodSecurityMetadataSource;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.zspace.common.security.rating.config.annotation.configuration.Rating;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 方法注解元数据获取 -- 获取访问频率的注解数据
 */
public class MethodRatingMetadataSource extends AbstractFallbackMethodSecurityMetadataSource {

    /**
     * 对方法注解进行缓存
     */
    protected final Map<MethodRatingMetadataSource.RegisteredMethod, List<ConfigAttribute>> methodMap = new HashMap<>();

    @Override
    protected Collection<ConfigAttribute> findAttributes(Method method, Class<?> targetClass) {

        MethodRatingMetadataSource.RegisteredMethod registeredMethod = new MethodRatingMetadataSource.RegisteredMethod(method, targetClass);
        if (methodMap.containsKey(registeredMethod)) {
            return (List<ConfigAttribute>) methodMap.get(registeredMethod);
        }

        Rating ratingMeta = AnnotationUtils.getAnnotation(method, Rating.class);
        if(ObjectUtils.isEmpty(ratingMeta)){
            return null;
        }
        List<ConfigAttribute> lists = processAnnotations(new Annotation[]{ratingMeta});
        methodMap.put(registeredMethod, lists);
        return lists;
    }

    @Override
    protected Collection<ConfigAttribute> findAttributes(Class<?> clazz) {
        return processAnnotations(clazz.getAnnotations());
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    private List<ConfigAttribute> processAnnotations(Annotation[] annotations) {
        if (annotations == null || annotations.length == 0) {
            return null;
        }
        List<ConfigAttribute> attributes = new ArrayList<>();
        for (Annotation a : annotations) {
            if (a instanceof Rating) {
                attributes.add(new Jsr250SecurityConfig(String.valueOf(((Rating) a).value())));
                return attributes;
            }
        }
        return null;
    }

    private static class RegisteredMethod {
        private final Method method;
        private final Class<?> registeredJavaType;

        public RegisteredMethod(Method method, Class<?> registeredJavaType) {
            Assert.notNull(method, "Method required");
            Assert.notNull(registeredJavaType, "Registered Java Type required");
            this.method = method;
            this.registeredJavaType = registeredJavaType;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj != null && obj instanceof MethodRatingMetadataSource.RegisteredMethod) {
                MethodRatingMetadataSource.RegisteredMethod rhs = (MethodRatingMetadataSource.RegisteredMethod) obj;
                return method.equals(rhs.method)
                        && registeredJavaType.equals(rhs.registeredJavaType);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return method.hashCode() * registeredJavaType.hashCode();
        }

        @Override
        public String toString() {
            return "RegisteredMethod[" + registeredJavaType.getName() + "; " + method
                    + "]";
        }
    }

}