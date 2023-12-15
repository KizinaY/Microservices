package com.kizina.resourceservice.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@Profile("prod")
public class ProductionConfig {

    @Value("${AWS_REGION}")
    private String region;

    @Bean
    public S3Client amazonS3Client() {
        AwsCredentialsProvider credentialsProvider = EnvironmentVariableCredentialsProvider.create();
        return S3Client.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.of(region))
                .build();
    }
}
