package ru.pflb.homework.page;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.pflb.homework.builder.ProcessingThread;
import ru.pflb.homework.config.DriverManager;
import ru.pflb.homework.elementModels.AbstractElement;
import ru.pflb.homework.utils.CustomReflection;

public class AbstractPage {
    private WebDriverWait wait;
    private WebDriver driver;


    public AbstractPage() {
        this.driver = DriverManager.getWD(ProcessingThread.getSessionId());
        wait = new WebDriverWait(this.driver,10);
    }

    protected WebDriver getDriver() {
        return driver;
    }



    protected <T extends AbstractElement> T elementFactory(String name,Class<T> clazz, By by) {
        return CustomReflection.createNewInstanceOr(clazz,null, driver.findElement(by)).withCaption(name);
    }
}
