package ru.pflb.homework.page;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.pflb.homework.builder.ProcessingThread;
import ru.pflb.homework.config.DriverManager;

public class AbstractPage {
    private WebDriverWait wait;
    private WebDriver driver;

    public AbstractPage() {
        this.driver = DriverManager.getWD(((ProcessingThread)Thread.currentThread()).getSessionId());
        wait = new WebDriverWait(this.driver,10);
    }

    protected WebDriver getDriver() {
        return driver;
    }
}
