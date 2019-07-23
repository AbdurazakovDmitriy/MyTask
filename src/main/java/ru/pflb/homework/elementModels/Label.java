package ru.pflb.homework.elementModels;

import org.openqa.selenium.WebElement;
import ru.pflb.homework.interfaces.Clickable;
import ru.pflb.homework.interfaces.HasText;

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
        initialElement.click();
    }
}
