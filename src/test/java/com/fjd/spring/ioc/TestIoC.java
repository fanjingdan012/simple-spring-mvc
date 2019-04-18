package com.fjd.spring.ioc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestIoC {
    @Test
    public void testIoC() {
        // 1. 创建beanFactory
        BeanFactory beanFactory = new BeanFactory();

        // 2. 注册bean
        BeanDefinition bd = new BeanDefinition();
        bd.setBeanClassName("HelloWorld");

        BeanDefinitionHolder bdh = new BeanDefinitionHolder("helloWorld", bd);
        beanFactory.registerBeanDefinition(bdh.getBeanName(), bdh.getBeanDefinition());

        // 3. 获取bean
        HelloWorld hello = (HelloWorld) beanFactory.getBean("helloWorld");
        assertEquals("Hello World!", hello.sayHello());
    }

    @Test
    public void testIoCProperty() {
        // 1. 创建beanFactory
        BeanFactory beanFactory = new BeanFactory();

        // 2. 注册bean
        BeanDefinition bd = new BeanDefinition();
        bd.setBeanClassName("HelloWorld");

        // 注入Property
        PropertyValues pvs = new PropertyValues();
        pvs.addPropertyValue(new PropertyValue("msg", "Hello IoC Property!"));
        bd.setPropertyValues(pvs);

        BeanDefinitionHolder bdh = new BeanDefinitionHolder("helloWorld", bd);
        beanFactory.registerBeanDefinition(bdh.getBeanName(), bdh.getBeanDefinition());

        // 3. 获取bean
        HelloWorld hello = (HelloWorld) beanFactory.getBean("helloWorld");
        assertEquals("Hello IoC Property!", hello.sayHello());
    }
}
