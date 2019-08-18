package ru.pflb.homework.elementModels;

import org.openqa.selenium.WebElement;
import ru.pflb.homework.interfaces.Clickable;
import ru.pflb.homework.utils.CustomLogger;

public class Button extends AbstractElement implements Clickable {

    public Button(WebElement initialElement, String triggerName) {
        super(initialElement);
        if (triggerName != null && !triggerName.equals("")) {
        }
    }

    @Override
    public void click() {
        CustomLogger.info(String.format("Клик по элементу '%s'", getInitialElement().getText()));
        initialElement.click();
    }
}
