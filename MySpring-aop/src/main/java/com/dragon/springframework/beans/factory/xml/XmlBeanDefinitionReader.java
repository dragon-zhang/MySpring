package com.dragon.springframework.beans.factory.xml;

import com.dragon.springframework.beans.factory.BeanFactory;
import com.dragon.springframework.beans.factory.BeanFactoryAware;
import com.dragon.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import com.dragon.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

/**
 * Bean定义阅读器，将实际的XML文档读取工作委托给
 * {@link BeanDefinitionDocumentReader}接口的实现类，
 * 之后向给定的bean工厂注册每个bean定义。
 *
 * @author SuccessZhang
 * @date 2020/06/08
 */
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader implements BeanFactoryAware {

    private static final String CLASSPATH_URL_PREFIX = "classpath:";

    private static final String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

    private static final String XML = ".xml";

    private BeanFactory beanFactory;

    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    public int loadBeanDefinitions(String location) throws Exception {
        BeanDefinitionDocumentReader documentReader = new DefaultBeanDefinitionDocumentReader();
        String fileName = location.replace(CLASSPATH_URL_PREFIX, "")
                .replace(CLASSPATH_ALL_URL_PREFIX, "");
        if (fileName.endsWith(XML)) {
            int countBefore = getRegistry().getBeanDefinitionCount();
            //加载配置文件
            try (InputStream inputStream = getBeanClassLoader().getResourceAsStream(fileName)) {
                SAXReader xmlReader = new SAXReader();
                Document document = xmlReader.read(inputStream);
                //将bean的配置信息注册到伪IOC容器
                XmlReaderContext context = new XmlReaderContext(this);
                context.setBeanFactory(this.beanFactory);
                documentReader.registerBeanDefinitions(document, context);
            }
            return getRegistry().getBeanDefinitionCount() - countBefore;
        }
        return 0;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws Exception {
        this.beanFactory = beanFactory;
    }
}
