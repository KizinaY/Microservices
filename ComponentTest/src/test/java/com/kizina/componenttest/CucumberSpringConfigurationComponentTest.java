package com.kizina.componenttest;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = ComponentTestApplication.class)
public class CucumberSpringConfigurationComponentTest {

}
