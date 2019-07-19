package ru.pflb.homework;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import ru.pflb.homework.builder.Builder;
import ru.pflb.homework.config.Driver;
import ru.pflb.homework.config.DriverManager;
import ru.pflb.homework.page.AbstractPage;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class TestClass {
    @Test
    public void test() throws NoSuchFieldException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
//        WebDriver webDriver1 = DriverManager.get(Driver.Chrome);
//        WebDriverWait driverWait1 = new WebDriverWait(Objects.requireNonNull(webDriver1),10);
//        webDriver1.get("https://mail.google.com");
//        driverWait1.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//span[@class='RveJvd snByac']")));
//        Class clazz =  Objects.requireNonNull(Builder.buildPage(Builder.CHROME, "LoginPage"));
//        AbstractPage page = (AbstractPage) clazz.newInstance();
//        ((WebElement)page.getClass().getMethod("close").invoke(clazz.newInstance())).click();
//
//        webDriver1.close();



    }

}
