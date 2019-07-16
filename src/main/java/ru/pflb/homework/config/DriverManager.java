package ru.pflb.homework.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import ru.pflb.homework.builder.Builder;

import java.util.Objects;

public final class DriverManager {
    //todo а если использовать мапу - то не понадобится использовать миллиард свитчей
    private static ThreadLocal<WebDriver> chromeDriverContainer = new ThreadLocal<>();
    private static ThreadLocal<WebDriver> firefoxDriverContainer = new ThreadLocal<>();


    private DriverManager() {
    }
    //TODO а синхронизация?
    public static WebDriver get(Driver driver) {
        switch (driver) {
            case CHROME_DRIVER: {
                WebDriver webDriver = chromeDriverContainer.get();
                if (webDriver != null) {
                    return webDriver;
                }
            }
            break;
            case FIREFOX_DRIVER: {
                WebDriver webDriver = firefoxDriverContainer.get();
                if (webDriver != null) {
                    return webDriver;
                }
            }
            break;
            default: {
                ////////////////////////////
            }
        }






        switch (driver) {
            case CHROME_DRIVER: {
                System.setProperty("webdriver.chrome.driver", Objects.requireNonNull(Builder.parseDriverPath("Chrome")));
                chromeDriverContainer.set(new ChromeDriver());
                return chromeDriverContainer.get();
            }
            case FIREFOX_DRIVER: {
                System.setProperty("webdriver.gecko.driver", Objects.requireNonNull(Builder.parseDriverPath("Firefox")));
                firefoxDriverContainer.set(new FirefoxDriver());
                return firefoxDriverContainer.get();
            }
            default:
                ////////////////////
        }
        return null;
    }
    //TODO Поскольку это активно используемый енам, я бы советовал вынести его в отдельный класс
    public static enum Driver {
        CHROME_DRIVER,
        FIREFOX_DRIVER;

        public static Driver of(String browser) {
            switch (browser.toUpperCase()){
                case "CHROME": return CHROME_DRIVER;
                case "FIREFOX": return FIREFOX_DRIVER;
            }
            return null;
        }
    }


}
