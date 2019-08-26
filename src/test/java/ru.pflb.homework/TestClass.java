package ru.pflb.homework;

import org.testng.annotations.Test;
import ru.pflb.homework.builder.Builder;
import ru.pflb.homework.config.DriverManager;

import static ru.pflb.homework.builder.Builder.CHROME;
import static ru.pflb.homework.builder.Builder.FIREFOX;
import static ru.pflb.homework.builder.PageMapper.getPage;

public class TestClass {
    @Test
    public void test() {
//        Builder.buildPage(CHROME, "LoginPage");
//        DriverManager.get("ChromeDriver");
//        System.out.println(getPage("LoginPage").getClass().getName());

        Builder.buildPage(FIREFOX, "LoginPage");
        DriverManager.get("FirefoxDriver");
        System.out.println(getPage("LoginPage").getClass().getName());
    }
}
