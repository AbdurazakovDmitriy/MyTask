package ru.pflb.homework.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import ru.pflb.homework.annotations.Element;
import ru.pflb.homework.annotations.Page;
import ru.pflb.homework.annotations.Pages;
import ru.pflb.homework.config.DriverManager;
import ru.pflb.homework.elementModels.Label;
import ru.pflb.homework.builder.ProcessingThread;

@Page("LoginPage")
public class LoginPage extends AbstractPage {




















    public LoginPage() {
        super(((ProcessingThread)Thread.currentThread()).getSessionId());
    }
}