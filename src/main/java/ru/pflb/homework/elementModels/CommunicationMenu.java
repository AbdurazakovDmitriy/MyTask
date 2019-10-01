package ru.pflb.homework.elementModels;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import ru.pflb.homework.interfaces.Clickable;
import ru.pflb.homework.interfaces.Selectable;
import ru.pflb.homework.utils.CustomLogger;

public class CommunicationMenu extends AbstractElement implements Selectable {


    public CommunicationMenu(WebElement initialElement) {
        super(initialElement);
    }

    public void selectItem(String itemName) {
        Clickable item = (Clickable) getInitialElement().findElement(By.xpath(String.format(".//div[@data-tooltip='%s']", itemName)));
        CustomLogger.info(String.format("Клик по элементу меню '%s'", itemName));
        item.click();
    }



}
