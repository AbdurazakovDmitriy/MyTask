package ru.pflb.homework.stepdefinitions;

import cucumber.api.java.bg.И;
import org.openqa.selenium.WebDriver;
import ru.pflb.homework.annotations.Element;
import ru.pflb.homework.builder.Builder;
import ru.pflb.homework.builder.PageMapper;
import ru.pflb.homework.builder.ProcessingThread;
import ru.pflb.homework.config.DriverManager;
import ru.pflb.homework.interfaces.Clickable;
import ru.pflb.homework.utils.CustomLogger;
import ru.pflb.homework.utils.CustomReflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class MyStepdefs {
    @И("выполнена авторизация")
    public void auth() {
        String sessionId =  ProcessingThread.getSessionId();
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
        String sessionId =  ProcessingThread.getSessionId();
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
        Class pageClass =  Builder.buildPage(DriverManager.getWD(ProcessingThread.getSessionId()).getClass().getSimpleName(),pageName);
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
        String sessionId =  ProcessingThread.getSessionId();
        WebDriver driver =  DriverManager.getWD(sessionId);
        driver.get(url);
    }

    @И("появилась страница \"(.+)\"")
    public void getPage(String pageName) {
        Builder.buildPage(DriverManager.getWD(ProcessingThread.getSessionId()).getClass().getSimpleName(),pageName);
        PageMapper.getPage(pageName);
    }

    @И("нажать на элемент \"(.+)\"")
    public void clickOnElement(String elementName) throws IllegalAccessException, InvocationTargetException {
        Object activePage = PageMapper.getActivePage();
        Predicate<Element> predicate= (o1)->o1!=null&&o1.value().equals(elementName);
        Clickable element = (Clickable) CustomReflection
                .getMethods(activePage.getClass())
                .stream()
                .filter(o->predicate.test(o.getAnnotation(Element.class)))
                .findFirst().get().invoke(activePage);
        element.click();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
