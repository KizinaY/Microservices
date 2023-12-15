package com.kizina.resourceservice.controller;

import com.kizina.resourceservice.ResourceServiceApplication;
import com.kizina.resourceservice.producer.RabbitMQProducer;
import com.kizina.resourceservice.service.AudioService;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@CucumberContextConfiguration
@SpringBootTest(classes = ResourceServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration {

    @MockBean
    public AudioService mockAudioService;

    @MockBean
    public RabbitMQProducer rabbitMQProducer;
}
