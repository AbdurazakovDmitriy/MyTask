package ru.pflb.homework.page;


import org.openqa.selenium.support.ui.WebDriverWait;
import ru.pflb.homework.config.DriverManager;

public class AbstractPage {
    private WebDriverWait wait = new WebDriverWait(DriverManager.get(), 10);

    public static void setDriverType(String driverType) {
        driverType = driverType;
    }




}
