package ru.pflb.homework;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.testng.annotations.Test;
import ru.pflb.homework.config.DriverManager;
import ru.pflb.homework.utils.CustomLogger;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class TestClass {
    volatile Map<String, WebDriver> local = new Hashtable<>();
    @Test
    public void test() throws NoSuchMethodException, IOException, XMLStreamException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, InterruptedException {
//
//
//        FirefoxOptions firefoxOptions = new FirefoxOptions();
//        GeckoDriverService geckoDriverService = new GeckoDriverService.Builder().usingDriverExecutable(new File("D:\\Users\\user\\drivers\\geckodriver.exe")).build();
//        firefoxOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
//        firefoxOptions.addArguments("--start-maximized");
//        WebDriver firefoxDriver = new FirefoxDriver(geckoDriverService,firefoxOptions);
//
//
//
//        local.put("Firefox", firefoxDriver);
//
//
//        ChromeOptions chromeOptions1 = new ChromeOptions();
//        ChromeDriverService chromeDriverService = new ChromeDriverService.Builder().usingDriverExecutable(new File("D:\\Users\\user\\drivers\\chromedriver.exe")).build();
//        chromeOptions1.setPageLoadStrategy(PageLoadStrategy.NORMAL);
//        chromeOptions1.addArguments("--start-maximized");
//        WebDriver chromeDriver = new ChromeDriver(chromeDriverService, chromeOptions1);
//
//        local.put("Chrome",chromeDriver);

//        local.get().get("Firefox").get("https://www.google.com/search?q=sdf&oq=sdf&aqs=chrome..69i57j0l5.1059j0j8&sourceid=chrome&ie=UTF-8");
//        local.get().get("Chrome").get("https://www.google.com/search?q=sdf&oq=sdf&aqs=chrome..69i57j0l5.1059j0j8&sourceid=chrome&ie=UTF-8");
        //local.get().get("Firefox").get("https://www.google.com/search?q=sdf&oq=sdf&aqs=chrome..69i57j0l5.1059j0j8&sourceid=chrome&ie=UTF-8");
        //todo работает в двух потоках. раскоментить
        DriverManager.getDW("FirefoxDriver");

//        ChromeRunner chromeRunner = new ChromeRunner();
//        FirefoxRunner firefoxRunner = new FirefoxRunner();
//        chromeRunner.start();
//        firefoxRunner.start();
//        Thread.sleep(1000);
    }

    class ChromeRunner extends Thread {
        @Override
        public void run() {
            CustomLogger.debug("Chrome");
            getLocal().get("Chrome").get("https://www.google.com/search?q=sdf&oq=sdf&aqs=chrome..69i57j0l5.1059j0j8&sourceid=chrome&ie=UTF-8");
        }
    }

    class FirefoxRunner extends Thread {
        public void run() {
            CustomLogger.debug("Firefox");
            getLocal().get("Firefox").get("https://www.google.com/search?q=sdf&oq=sdf&aqs=chrome..69i57j0l5.1059j0j8&sourceid=chrome&ie=UTF-8");
        }
    }

    private   Map<String, WebDriver> getLocal() {
        return this.local;
    }
}