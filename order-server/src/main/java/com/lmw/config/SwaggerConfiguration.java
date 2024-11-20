package com.lmw.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.WebFluxRequestHandlerProvider;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;

@Configuration
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)//导入其他的配置类 让配置生效
public class SwaggerConfiguration {

    @Bean
    public Docket buildDocket() {
        HashSet<String> strings = new HashSet<>();
        strings.add("application/json");
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(buildApiInfo())
                //设置返回数据类型
                .produces(strings)
                //分组名称
                .groupName("1.0")
                .pathMapping("/")
                .select()
                //这里指定扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.lmw.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public ApiInfo buildApiInfo() {
        Contact contact = new Contact("赖敏伟", "https://github.com/supbyte/order-service", "2219084706@qq.com");
        return new ApiInfoBuilder()
                .title("订单服务API文档")
                .description("所有接口均已测试通过")
                .contact(contact)
                .version("1.0.0").build();
    }

    /**
     * 创建并返回一个BeanPostProcessor实例，用于处理Springfox的请求处理提供者
     * 这个方法是一个Bean方法，通过注解@Bean标记，告诉Spring框架在启动时创建这个Bean
     *
     * @return BeanPostProcessor实例，用于定制Springfox的处理映射
     */
    @Bean
    public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return new BeanPostProcessor() {

            /**
             * 在Bean初始化完成后进行处理
             * 该方法主要用于定制Springfox的请求处理映射，通过移除具有特定模式解析器的映射
             *
             * @param bean 初始化后的Bean实例
             * @param beanName Bean的名称
             * @return 处理后的Bean实例
             * @throws BeansException 如果处理过程中发生异常
             */
            @Override
            @SuppressWarnings("all")
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                // 检查Bean是否是WebMvcRequestHandlerProvider或WebFluxRequestHandlerProvider的实例
                if (bean instanceof WebMvcRequestHandlerProvider || bean instanceof WebFluxRequestHandlerProvider) {
                    // 如果是，调用方法定制Springfox的处理映射
                    customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
                }
                return bean;
            }

            /**
             * 定制Springfox的处理映射
             * 该方法通过移除具有特定模式解析器的请求映射来定制Springfox的处理映射
             *
             * @param mappings 请求映射列表
             */
            private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(List<T> mappings) {
                // 移除所有具有非空模式解析器的映射
                mappings.removeIf(mapping -> mapping.getPatternParser() != null);
            }

            /**
             * 获取处理映射列表
             * 该方法通过反射获取Bean中的处理映射列表
             *
             * @param bean 包含处理映射的Bean实例
             * @return 处理映射列表
             * @throws IllegalArgumentException 如果反射操作失败
             * @throws IllegalAccessException 如果反射操作失败
             */
            @SuppressWarnings("all")
            private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
                try {
                    // 使用反射找到并获取Bean中的handlerMappings字段
                    Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
                    field.setAccessible(true);
                    return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        };
    }


}
