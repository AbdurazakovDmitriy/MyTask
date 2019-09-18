package ru.pflb.homework;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;
import ru.pflb.homework.builder.Builder;
import ru.pflb.homework.builder.PageMapper;
import ru.pflb.homework.config.DriverManager;
import ru.pflb.homework.utils.CustomLogger;

public class TestClass {

    @Test
    public void test(){
//        ChromeRunner chromeRunner = new ChromeRunner();
//        FirefoxRunner firefoxRunner = new FirefoxRunner();
//        chromeRunner.start();
//        firefoxRunner.start();
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        String s;
        s= PageMapper.getPage("LoginPage").getClass().getSimpleName();
        System.out.println(s);
    }

    class ChromeRunner extends Thread {
        @Override
        public void run() {
            CustomLogger.debug("ChromeDriver");
            WebDriver driver =  DriverManager.getWD("ChromeDriver");
            driver.get("https://www.google.com");

//            driver.close();

        }
    }

    class FirefoxRunner extends Thread {
        public void run() {
            CustomLogger.debug("FirefoxDriver");
            WebDriver driver =  DriverManager.getWD("FirefoxDriver");
            driver.get("https://www.google.com");

//            driver.close();
        }
    }

}