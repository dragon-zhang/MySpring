package com.dragon.springframework.beans.factory.support;

import lombok.Setter;

/**
 * 实现{@link BeanDefinitionReader}接口的bean定义阅读器
 * 的抽象基类，提供了一些通用属性。
 *
 * @author SuccessZhang
 * @date 2020/06/08
 */
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {

    private final BeanDefinitionRegistry registry;

    @Setter
    private ClassLoader beanClassLoader = ClassLoader.getSystemClassLoader();

    public AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    @Override
    public BeanDefinitionRegistry getRegistry() {
        return this.registry;
    }

    @Override
    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

    @Override
    public int loadBeanDefinitions(String... locations) throws Exception {
        int result = 0;
        for (String location : locations) {
            result += loadBeanDefinitions(location);
        }
        return result;
    }
}
