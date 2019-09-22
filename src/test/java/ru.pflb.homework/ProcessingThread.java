package ru.pflb.homework;

import cucumber.api.testng.PickleEventWrapper;
import ru.pflb.homework.config.DriverManager;

import java.util.UUID;

public class ProcessingThread extends Thread {
    private LocalTestNgRunner runner;
    private String sessionId;

    public ProcessingThread(String driverType, LocalTestNgRunner runner) {
        this.runner = runner;
        sessionId = UUID.randomUUID().toString();
        DriverManager.local.put(sessionId, DriverManager.getWD(driverType));
    }

    public void run() {
        Object[][] scenarios = runner.provideScenarios();
        try {
            runner.runScenario(((PickleEventWrapper) scenarios[0][0]).getPickleEvent());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public String getSessionId() {
        return sessionId;
    }
}
