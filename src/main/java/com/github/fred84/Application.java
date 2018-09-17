package com.github.fred84;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.lang.reflect.Method;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.transformer.AbstractPayloadTransformer;

@EnableIntegration
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public DirectChannel channel1() {
        return new DirectChannel();
    }

    @Bean
    public DirectChannel channel2() {
        return new DirectChannel();
    }

    @Bean // remove this bean to fix Transformer creation
    public JavaTimeModule timeModule() {
        return new JavaTimeModule();
    }

    /**
     * This method depends on object mapper, which is not yet configured (with module above) when
     * Spring DI is trying to instantiate transformer.
     *
     * Useful debug points:
     * {@link org.springframework.beans.factory.support.DefaultListableBeanFactory#getBeansOfType(Class, boolean, boolean)}
     * {@link org.springframework.integration.config.annotation.TransformerAnnotationPostProcessor#createHandler(Object, Method, List)}
     *
     */
    @Bean
    @Transformer(inputChannel = "channel1", outputChannel = "channel2")
    public AbstractPayloadTransformer transformer(ObjectMapper dependency) {
        return new AbstractPayloadTransformer<Object, Object>() {
            @Override
            protected Object transformPayload(Object payload) {
                return payload;
            }
        };
    }
}