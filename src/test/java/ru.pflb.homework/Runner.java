package ru.pflb.homework;

import cucumber.api.CucumberOptions;
import cucumber.api.testng.CucumberFeatureWrapper;
import cucumber.api.testng.PickleEventWrapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.pflb.homework.builder.LocalTestNgRunner;
import ru.pflb.homework.builder.ProcessingThread;
import ru.pflb.homework.utils.CustomLogger;

import java.util.*;
import java.util.stream.Collectors;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = "ru.pflb.homework.stepdefinitions"
)
public class Runner {
    LocalTestNgRunner runner;
    private List<ProcessingThread> runners;

    @BeforeClass
    public void setUpClass() {
        runner = new LocalTestNgRunner(this.getClass());
        runners = new ArrayList<>();
    }

    @DataProvider
    public Object[][] features() {
        Object[][] scenarios = runner.provideScenarios();
        ((PickleEventWrapper)scenarios[0][0]).getPickleEvent();
        return scenarios;
    }

    @Test
    public void numberedScenario()  {
        System.setProperty("scenario.data","{2.0:ChromeDriver}");
        String scenariosData = System.getProperty("scenario.data");
        List<String> threadData =  Arrays.stream(scenariosData.split("\\}\\{")).map(o->o.replaceAll("[{}]","")).collect(Collectors.toList());

        threadData.forEach(o->{
            String[] numbers = o.replaceAll(":.+","").split(",");
            String driverType = o.replaceAll(".+:","");
            ArrayList<Object[]> filteredScenarios = new ArrayList();
            scenarios_loop:
            for (Object[] scenarios : runner.provideScenarios()) {
                ArrayList<Object> featureContents = new ArrayList<>();
                for (Object scenario : scenarios) {
                    if (scenario instanceof CucumberFeatureWrapper) {
                        if (featureContents.size() == 0) continue scenarios_loop;
                        featureContents.add(scenario);
                        break;
                    }
                    String scenarioName = ((PickleEventWrapper) scenario).getPickleEvent().pickle.getName().trim();
                    for (String number : numbers) {
                        if (scenarioName.startsWith(number.trim())) {
                            featureContents.add(scenario);
                            break;
                        }
                    }
                }
                filteredScenarios.add(featureContents.toArray());
            }
            Object[][] results = new Object[filteredScenarios.size()][];
            for (int i = 0; i < filteredScenarios.size(); i++) {
                results[i] = filteredScenarios.get(i);
            }

            LocalTestNgRunner localTestNgRunner = new LocalTestNgRunner(this.getClass()) {
                @Override
                public Object[][] provideScenarios() {
                    return results;
                }
            };
            ProcessingThread processingThread = new ProcessingThread(driverType,localTestNgRunner);
            runners.add(processingThread);
        });
    }

    @AfterClass
    public void start() {

        runners.forEach(Thread::start);
        for (ProcessingThread processingThread : runners) {
            try {
                processingThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }






//    @Test(dataProvider = "features")
//    public void test(PickleEventWrapper pickleEventWrapper, CucumberFeatureWrapper cucumberFeature) throws Throwable {
//        CustomLogger.info(String.format("Запуск сценария %s", cucumberFeature.toString()));
//        runner.runScenario(pickleEventWrapper.getPickleEvent());
//    }
}
