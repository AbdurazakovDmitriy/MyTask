package ru.pflb.homework.builder;

import cucumber.api.testng.PickleEventWrapper;
import ru.pflb.homework.config.DriverManager;

import java.util.UUID;

public class ProcessingThread extends Thread {
    private LocalTestNgRunner runner;
    private String sessionId;

    public ProcessingThread(String driverType, LocalTestNgRunner runner) {
        this.runner = runner;
        sessionId = UUID.randomUUID().toString();
        DriverManager.setWD(sessionId, DriverManager.createWD(driverType));
    }
    @Override
    public void run() {
        Object[][] scenarios = runner.provideScenarios();
        try {
            for(Object[] scenario: scenarios) {
                runner.runScenario(((PickleEventWrapper)scenario[0]).getPickleEvent());
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public String getSessionId() {
        return sessionId;
    }
}
