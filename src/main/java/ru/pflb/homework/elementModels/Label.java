package ru.pflb.homework.elementModels;

import org.openqa.selenium.WebElement;
import ru.pflb.homework.interfaces.Clickable;
import ru.pflb.homework.interfaces.HasText;
import ru.pflb.homework.utils.CustomLogger;

public class Label extends AbstractElement implements HasText, Clickable {
    public Label(WebElement initialElement) {
        super(initialElement);
    }

    @Override
    public String getText() {
        return initialElement.getText();
    }

    @Override
    public void click() {
        CustomLogger.info(String.format("Клик по элементу '%s'", getInitialElement().getText()));
        initialElement.click();
    }
}
