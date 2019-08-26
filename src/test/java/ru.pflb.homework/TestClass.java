package ru.pflb.homework;

import org.testng.annotations.Test;
import ru.pflb.homework.builder.Builder;
import ru.pflb.homework.builder.PageMapper;
import ru.pflb.homework.config.DriverManager;

import static ru.pflb.homework.builder.Builder.CHROME;
import static ru.pflb.homework.builder.Builder.FIREFOX;
import static ru.pflb.homework.builder.PageMapper.getPage;

public class TestClass {
    @Test
    public void test() throws InterruptedException {
        FirefoxRunner firefoxRunner = new FirefoxRunner();
        ChromeRunner chromeRunner = new ChromeRunner();
        firefoxRunner.start();
        firefoxRunner.join();

        chromeRunner.start();
        chromeRunner.join();



    }
}

class FirefoxRunner extends Thread {
    @Override
    public void run() {
        Builder.buildPage(FIREFOX, "LoginPage");
        DriverManager.get("FirefoxDriver");
        System.out.println(getPage("LoginPage").getClass().getName());
    }
}

class ChromeRunner extends Thread {
    @Override
    public void run() {

        Builder.buildPage(CHROME, "LoginPage");
        DriverManager.get("ChromeDriver");
        System.out.println(getPage("LoginPage").getClass().getName());
    }
}
