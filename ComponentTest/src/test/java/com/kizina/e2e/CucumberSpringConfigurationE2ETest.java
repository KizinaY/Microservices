package com.kizina.e2e;

import com.kizina.componenttest.ComponentTestApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import jakarta.annotation.PostConstruct;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = ComponentTestApplication.class)
public class CucumberSpringConfigurationE2ETest {
}
