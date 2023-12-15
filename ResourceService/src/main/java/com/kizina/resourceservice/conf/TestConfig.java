package com.kizina.resourceservice.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@Profile("test")
public class TestConfig {

    @Bean
    public S3Client amazonS3Client() {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create("test_access_key", "test_secret_key");
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .region(Region.EU_NORTH_1)
                .build();
    }
}
