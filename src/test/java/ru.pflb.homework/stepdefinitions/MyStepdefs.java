package ru.pflb.homework.stepdefinitions;

import cucumber.api.java.bg.И;
import org.openqa.selenium.WebDriver;
import ru.pflb.homework.builder.Builder;
import ru.pflb.homework.builder.PageMapper;
import ru.pflb.homework.builder.ProcessingThread;
import ru.pflb.homework.config.DriverManager;
import ru.pflb.homework.utils.CustomLogger;

public class MyStepdefs {
    @И("выполнена авторизация")
    public void auth() {
        String sessionId =  ((ProcessingThread)Thread.currentThread()).getSessionId();
        WebDriver driver =  DriverManager.getWD(sessionId);
        driver.get("https://github.com/AbdurazakovDmitriy/MyTask");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @И("выполнена авторизация яндекс")
    public void auth2() {
        String sessionId =  ((ProcessingThread)Thread.currentThread()).getSessionId();
        WebDriver driver =  DriverManager.getWD(sessionId);
        driver.get("https://yandex.ru");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @И("создать страницу \"(.+)\"")
    public void pageCreate(String pageName) {
        Class pageClass =  Builder.buildPage(DriverManager.getWD(((ProcessingThread)Thread.currentThread()).getSessionId()).getClass().getSimpleName(),pageName);
        String page =  PageMapper.getPage(pageName).getClass().getName();
        CustomLogger.info(page);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @И("открыть почту")
    public void openGMail() {
        String url = "https://www.gmail.com/mail/help/intl/ru/about.html?de";
        String sessionId =  ((ProcessingThread)Thread.currentThread()).getSessionId();
        WebDriver driver =  DriverManager.getWD(sessionId);
        driver.get(url);
    }


}
