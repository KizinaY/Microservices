package com.kizina.resourceservice.controller;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.kizina.resourceservice.controller"},
        plugin = {"pretty", "json:target/cucumber.json"}
)
public class AudioControllerContractTest {
}
