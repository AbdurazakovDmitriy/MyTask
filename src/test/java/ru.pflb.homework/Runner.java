package ru.pflb.homework;

import cucumber.api.CucumberOptions;
import cucumber.api.testng.CucumberFeatureWrapper;
import cucumber.api.testng.PickleEventWrapper;
import cucumber.api.testng.TestNGCucumberRunner;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.pflb.homework.utils.CustomLogger;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = "ru.pflb.homework.stepdefinitions"
)
public class Runner {
    TestNGCucumberRunner runner;

    @BeforeClass
    public void setUpClass() {
        runner = new TestNGCucumberRunner(this.getClass());
    }

    @DataProvider
    public Object[][] features() {
        Object[][] scenarios = runner.provideScenarios();
        ((PickleEventWrapper)scenarios[0][0]).getPickleEvent();
        return scenarios;
    }

    @Test(dataProvider = "features")
    public void test(PickleEventWrapper pickleEventWrapper, CucumberFeatureWrapper cucumberFeature) throws Throwable {
        CustomLogger.info(String.format("Запуск сценария %s", cucumberFeature.toString()));
        runner.runScenario(pickleEventWrapper.getPickleEvent());
    }
}
