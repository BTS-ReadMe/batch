package com.readme.batch.config;

import com.readme.batch.requestObject.RequestPlusViewCount;
import com.readme.batch.responseObject.ResponseSearch;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String servers;

    @Bean
    public Map<String, Object> ConsumerConfig() {
        return CommonJsonDeserializer.getStringObjectMap(servers);
    }
    @Bean
    public ConsumerFactory<String, RequestPlusViewCount> viewCountFactory() {
        return new DefaultKafkaConsumerFactory<>(ConsumerConfig());
    }

    @Bean
    public ConsumerFactory<String, ResponseSearch> searchFactory() {
        return new DefaultKafkaConsumerFactory<>(ConsumerConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RequestPlusViewCount> viewCountListener() {
        ConcurrentKafkaListenerContainerFactory<String, RequestPlusViewCount> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(viewCountFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ResponseSearch> searchListener() {
        ConcurrentKafkaListenerContainerFactory<String, ResponseSearch> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(searchFactory());
        return factory;
    }

    @Bean
    public StringJsonMessageConverter jsonConverter() {
        return new StringJsonMessageConverter();
    }
}