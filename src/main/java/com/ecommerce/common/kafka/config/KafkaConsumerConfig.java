package com.ecommerce.common.kafka.config;

import com.ecommerce.common.kafka.event.OrderPlacedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}")
    private String bootstrapServers;

    @Value("${KAFKA_SECURITY_PROTOCOL:PLAINTEXT}")
    private String securityProtocol;

    @Value("${KAFKA_SASL_MECHANISM:PLAIN}")
    private String saslMechanism;

    @Value("${KAFKA_SASL_JAAS_CONFIG:}")
    private String saslJaasConfig;

    @Value("${KAFKA_SSL_TRUSTSTORE_LOCATION:}")
    private String sslTruststoreLocation;

    @Bean
    public ConsumerFactory<String, OrderPlacedEvent> consumerFactory() {

        JsonDeserializer<OrderPlacedEvent> deserializer =
                new JsonDeserializer<>(OrderPlacedEvent.class);

        deserializer.addTrustedPackages("com.ecommerce.*");
        deserializer.setRemoveTypeHeaders(false);
        deserializer.setUseTypeMapperForKey(false);

        Map<String, Object> props = new HashMap<>();

        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "ecommerce-group"
        );

        props.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest"
        );

        props.put(
                "security.protocol",
                securityProtocol
        );

        props.put(
                "sasl.mechanism",
                saslMechanism
        );

        if (!saslJaasConfig.isBlank()) {
            props.put(
                    "sasl.jaas.config",
                    saslJaasConfig
            );
        }

        if (!sslTruststoreLocation.isBlank()) {
            props.put(
                    "ssl.truststore.type",
                    "PEM"
            );

            props.put(
                    "ssl.truststore.location",
                    sslTruststoreLocation
            );
        }

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderPlacedEvent>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, OrderPlacedEvent>
                factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        return factory;
    }
}