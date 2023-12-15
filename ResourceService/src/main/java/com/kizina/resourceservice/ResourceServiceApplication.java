package com.kizina.resourceservice;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@EnableRetry
public class ResourceServiceApplication {

    @Value("${exchange.name.audio}")
    private String AUDIO_EXCHANGE_NAME;
    @Value("${queue.name.audio}")
    private String AUDIO_QUEUE_NAME;
    @Value("${exchange.name.processing}")
    private String PROCESSING_EXCHANGE_NAME;
    @Value("${queue.name.processing}")
    private String PROCESSING_QUEUE_NAME;


    public static void main(String[] args) {
        SpringApplication.run(ResourceServiceApplication.class, args);
    }

    @Bean
    public OpenAPI usersMicroserviceOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("ResourceService")
                        .description("Process audio")
                        .version("1.0"));
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    @Bean
    public Exchange processingExchange() {
        return new DirectExchange(PROCESSING_EXCHANGE_NAME);
    }

    @Bean
    public Queue processingQueue() {
        return new Queue(PROCESSING_QUEUE_NAME, true);
    }

    @Bean
    public Binding bindingProcessingQueueAndExchange(Queue processingQueue, Exchange processingExchange) {
        return BindingBuilder.bind(processingQueue).to(processingExchange).with(PROCESSING_QUEUE_NAME).noargs();
    }

    @Bean
    public Exchange audioExchange() {
        return new DirectExchange(AUDIO_EXCHANGE_NAME);
    }

    @Bean
    public Queue audioQueue() {
        return new Queue(AUDIO_QUEUE_NAME, true);
    }

    @Bean
    public Binding bindingAudioQueueAndExchange(Queue audioQueue, Exchange audioExchange) {
        return BindingBuilder.bind(audioQueue).to(audioExchange).with(AUDIO_QUEUE_NAME).noargs();
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
