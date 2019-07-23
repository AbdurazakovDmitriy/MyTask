package ru.pflb.homework.elementModels;

import org.openqa.selenium.WebElement;
import ru.pflb.homework.interfaces.Clickable;

import java.util.function.Consumer;

public class Button extends AbstractElement implements Clickable {
    private Consumer<Button> onClick;

    public Button(WebElement initialElement, String triggerName) {
        super(initialElement);
        if (triggerName != null && !triggerName.equals("")) {
//            this.onClick = Triggers.from(triggerName);
        }
    }

    @Override
    public void click() {

        initialElement.click();
        onClick.accept(this);
    }
}
