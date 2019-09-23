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
@Element("info")
public Label info(){
 return (Label)getDriver().findElement(By.xpath("//div[@class='info']"));
}

@Element("open")
public WebElement open(){
 return (WebElement)getDriver().findElement(By.xpath("//span[@class='RveJvd snByac']"));
}

@Element("clientType")
public Label clientType(){
 return (Label)getDriver().findElement(By.xpath("//span[@class='RveJvd snByac']"));
}

@Element("close")
public WebElement close(){
 return (WebElement)getDriver().findElement(By.xpath("xpath for chromedriver"));
}

    public LoginPage() {
        super(((ProcessingThread)Thread.currentThread()).getSessionId());
    }
}
