package ru.pflb.homework.elementModels;

import org.openqa.selenium.WebElement;
import ru.pflb.homework.utils.CustomReflection;

public abstract class AbstractElement {
    protected WebElement initialElement;

    public AbstractElement(WebElement initialElement) {
        this.initialElement = initialElement;
    }

    public WebElement getInitialElement() {
        return initialElement;
    }


}
