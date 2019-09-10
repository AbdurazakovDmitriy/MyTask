package ru.pflb.homework;

import cucumber.api.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = "ru.pflb.homework.stepdefinitions"
)
public class Runner {
}
