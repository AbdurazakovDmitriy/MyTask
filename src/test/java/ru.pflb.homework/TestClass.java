package ru.pflb.homework;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.Map;

public class TestClass {
    @Test
    public void test() {
        ThreadLocal<Map<String, WebDriver>> local = new ThreadLocal<>();
//        Capabilities firefoxOptions = new FirefoxOptions();
//        GeckoDriverService geckoDriverService = new GeckoDriverService.Builder().usingDriverExecutable(new File("D:\\Users\\user\\drivers\\geckodriver.exe")).build();
//        firefoxOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
//        firefoxOptions.addArguments("--start-maximized");
//        WebDriver firefoxDriver = new FirefoxDriver(geckoDriverService,firefoxOptions);
//
//        Map<String, WebDriver> driverMap = new HashMap<>();
//        driverMap.put("Firefox", firefoxDriver);
//        local.set(driverMap);
//
//        ChromeOptions chromeOptions1 = new ChromeOptions();
//        ChromeDriverService chromeDriverService = new ChromeDriverService.Builder().usingDriverExecutable(new File("D:\\Users\\user\\drivers\\chromedriver.exe")).build();
//        chromeOptions1.setPageLoadStrategy(PageLoadStrategy.NORMAL);
//        chromeOptions1.addArguments("--start-maximized");
//        WebDriver chromeDriver = new ChromeDriver(chromeDriverService, chromeOptions1);
//
//        local.get().put("Chrome",chromeDriver);
//
//        local.get().get("Firefox").get("https://www.google.com/search?q=sdf&oq=sdf&aqs=chrome..69i57j0l5.1059j0j8&sourceid=chrome&ie=UTF-8");
//        local.get().get("Chrome").get("https://www.google.com/search?q=sdf&oq=sdf&aqs=chrome..69i57j0l5.1059j0j8&sourceid=chrome&ie=UTF-8");
        //todo работает в двух потоках. раскоментить





    }
}