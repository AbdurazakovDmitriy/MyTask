package ru.pflb.homework.config;

import org.openqa.selenium.WebDriver;
import ru.pflb.homework.builder.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class DriverManager {
    private static WebDriver webDriver;

    private DriverManager() {
    }

    public static synchronized WebDriver get(String driverType) {
        if (driverType != null && !driverType.equals("") && webDriver != null && webDriver.getClass().getSimpleName().equals(driverType)) {
            return webDriver;
        } else {
            try (StaxStreamProcessor processor = new StaxStreamProcessor(Files.newInputStream(Paths.get("./src/main/resources/PageXmlSources.xml")))) {
                XMLStreamReader reader = processor.getReader();
                while (reader.hasNext()) {
                    int event = reader.next();
                    if (event == XMLEvent.START_ELEMENT && reader.getLocalName().equals("Driver") && reader.getAttributeValue(null, "type").equals(driverType)) {
                        System.setProperty(reader.getAttributeValue(null, "key"), reader.getAttributeValue(null, "filePath"));
                        Class<?> clazz = Class.forName(reader.getAttributeValue(null, "type"));
                        webDriver = (WebDriver) clazz.newInstance();
                    }
                }
            } catch (XMLStreamException | IOException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            return webDriver;
        }
    }

    public static synchronized WebDriver get() {
        return webDriver;
    }


}
