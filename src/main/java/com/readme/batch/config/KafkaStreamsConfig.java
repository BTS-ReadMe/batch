package com.readme.batch.config;

import com.readme.batch.responseObject.ResponseSearch;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@Slf4j
public class KafkaStreamsConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private String inputTopic = "inputSearch";

    private String outputTopic = "outputSearch";

    @Bean
    public StreamsBuilderFactoryBean  streamsBuilder(KafkaStreamsConfiguration kafkaStreamsConfigs) {
        return new StreamsBuilderFactoryBean(kafkaStreamsConfigs);
    }

    @Bean
    public KafkaStreamsConfiguration kafkaStreamsConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "search-keywords");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, ResponseSearchSerde.class);
        return new KafkaStreamsConfiguration(props);
    }

    public class ResponseSearchSerde extends Serdes.WrapperSerde<ResponseSearch> {
        public ResponseSearchSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(ResponseSearch.class));
        }
    }

    @Bean
    public KStream<String, String> kStream(StreamsBuilder builder) {
        KStream<String, String> stream = builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String()));
        KTable<Windowed<String>, Long> countedKeywords = stream
            .groupBy((key, word) -> word, Grouped.with(Serdes.String(), Serdes.String()))
            .windowedBy(TimeWindows.of(Duration.ofSeconds(10)))
//            .windowedBy(TimeWindows.of(Duration.ofMinutes(1)))
            .count();

        Serde<ResponseSearch> responseSearchSerde = new ResponseSearchSerde();

        countedKeywords.toStream().map((key, value) -> new KeyValue<>(key.key(), new ResponseSearch(key.key(), value)))
            .to(outputTopic, Produced.with(Serdes.String(), responseSearchSerde));
        return stream;
    }
    
}