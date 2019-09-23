package ru.pflb.homework;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.pflb.homework.builder.Builder;
import ru.pflb.homework.builder.PageMapper;
import ru.pflb.homework.config.DriverManager;
import ru.pflb.homework.utils.CustomLogger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestClass {
    private ThreadLocal<Map<WebDriver,String>> local;
    @DataProvider(name = "testData")
    public Object[][] dataprovider() {
        return new Object[][]{{"ChromeDriver"}};
    }

    @BeforeTest
    public void setUp() {
        local = new ThreadLocal<>();
    }

    @AfterTest
    public void tearDown() {

    }



    @Test(dataProvider = "testData")
    public void test(String driverType){

        Builder.buildPage(driverType, "LoginPage");
        PageMapper.getPage("LoginPage");
        WebDriver driver =  DriverManager.createWD(driverType);
        Map<WebDriver,String> map = new ConcurrentHashMap<>();
        map.put(driver,driverType);
        local.set(map);
        driver.get("https://translate.google.com/?hl=ru");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}