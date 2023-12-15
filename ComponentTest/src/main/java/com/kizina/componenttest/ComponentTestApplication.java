package com.kizina.componenttest;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootApplication
public class ComponentTestApplication {

    @Value("${exchange.name.audio}")
    private String AUDIO_EXCHANGE_NAME;
    @Value("${queue.name.audio}")
    private String AUDIO_QUEUE_NAME;
    @Value("${access.key}")
    private String TEST_ACCESS_KEY;
    @Value("${secret.key}")
    private String TEST_SECRET_KEY;
    @Value("${session.token}")
    private String TEST_SESSION_TOKEN;

    public static void main(String[] args) {
        SpringApplication.run(ComponentTestApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
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

    @Bean
    public S3Client amazonS3Client() {
        AwsSessionCredentials awsSessionCredentials = AwsSessionCredentials.create(
                TEST_ACCESS_KEY,
                TEST_SECRET_KEY,
                TEST_SESSION_TOKEN
        );
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsSessionCredentials))
                .region(Region.EU_NORTH_1)
                .build();
    }

}
