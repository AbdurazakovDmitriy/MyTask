package ru.pflb.homework.elementModels;

import org.openqa.selenium.WebElement;
import ru.pflb.homework.annotations.Element;
import ru.pflb.homework.interfaces.HasText;
import ru.pflb.homework.interfaces.TextEditable;
import ru.pflb.homework.utils.CustomLogger;

public class TextField extends AbstractElement implements TextEditable, HasText {
    public TextField(WebElement initialElement) {
        super(initialElement);
    }

    @Override
    public String getText() {
        return getInitialElement().getText();
    }

    @Override
    public void setText(String text) {
        CustomLogger.info(String.format("В поле '%s' установлено значение '%s'", caption, text));
        getInitialElement().sendKeys(text);
    }
}
