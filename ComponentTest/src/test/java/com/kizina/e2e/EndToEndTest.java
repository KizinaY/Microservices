package com.kizina.e2e;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/endtoend/features",
        glue = {"com/kizina/componenttest"},
        plugin = {"pretty", "json:target/cucumber.json"}
)
public class EndToEndTest {
}
