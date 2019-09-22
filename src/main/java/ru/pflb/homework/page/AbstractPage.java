package ru.pflb.homework.page;


import org.openqa.selenium.support.ui.WebDriverWait;
import ru.pflb.homework.config.DriverManager;

public class AbstractPage {
    private WebDriverWait wait;


    public AbstractPage(String sessionId) {
        wait = new WebDriverWait(DriverManager.local.get(sessionId),10);
    }

}
