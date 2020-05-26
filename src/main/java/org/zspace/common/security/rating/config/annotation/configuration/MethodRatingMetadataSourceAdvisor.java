package org.zspace.common.security.rating.config.annotation.configuration;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * 对于sprng AOP 代理生成的时候使用的 Advisor
 */
public class MethodRatingMetadataSourceAdvisor extends AbstractPointcutAdvisor
        implements BeanFactoryAware {

    private transient MethodSecurityMetadataSource attributeSource;
    private transient MethodInterceptor interceptor;
    private final Pointcut pointcut = new MethodSecurityMetadataSourcePointcut();
    private BeanFactory beanFactory;
    private final String adviceBeanName;
    private final String metadataSourceBeanName;
    private transient volatile Object adviceMonitor = new Object();

    public MethodRatingMetadataSourceAdvisor(String adviceBeanName,
                                             MethodSecurityMetadataSource attributeSource, String attributeSourceBeanName) {
        Assert.notNull(adviceBeanName, "The adviceBeanName cannot be null");
        Assert.notNull(attributeSource, "The attributeSource cannot be null");
        Assert.notNull(attributeSourceBeanName,
                "The attributeSourceBeanName cannot be null");

        this.adviceBeanName = adviceBeanName;
        this.attributeSource = attributeSource;
        this.metadataSourceBeanName = attributeSourceBeanName;
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        synchronized (this.adviceMonitor) {
            if (interceptor == null) {
                Assert.notNull(adviceBeanName,
                        "'adviceBeanName' must be set for use with bean factory lookup.");
                Assert.state(beanFactory != null,
                        "BeanFactory must be set to resolve 'adviceBeanName'");
                interceptor = beanFactory.getBean(this.adviceBeanName,
                        MethodInterceptor.class);
            }
            return interceptor;
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    class MethodSecurityMetadataSourcePointcut extends StaticMethodMatcherPointcut
            implements Serializable {
        @Override
        @SuppressWarnings("unchecked")
        public boolean matches(Method m, Class targetClass) {
            Collection attributes = attributeSource.getAttributes(m, targetClass);
            return attributes != null && !attributes.isEmpty();
        }
    }
}
