package ru.pflb.homework.config;

import org.openqa.selenium.WebDriver;

public final class DriverManager {
    private static ThreadLocal<WebDriver> driverContainer = new ThreadLocal<>();
    private DriverManager(){}

    public WebDriver get() {
        return null;
    }
}
