package ru.pflb.homework.config;

import org.openqa.selenium.WebDriver;
import ru.pflb.homework.builder.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class DriverManager {
    private static ThreadLocal<Map<String, WebDriver>> drivers;

    static {
        drivers = new ThreadLocal<>();
    }

    //todo sessionId

    public static synchronized WebDriver get(String driverType) {
        if (driverType != null && !driverType.equals("") && drivers.get() != null && drivers.get().containsKey(driverType)) {
            return drivers.get().get(driverType);
        } else {
            try (StaxStreamProcessor processor = new StaxStreamProcessor(Files.newInputStream(Paths.get("./src/main/resources/PageXmlSources.xml")))) {
                XMLStreamReader reader = processor.getReader();
                while (reader.hasNext()) {
                    int event = reader.next();
                    if (event == XMLEvent.START_ELEMENT && reader.getLocalName().equals("Driver") && reader.getAttributeValue(null, "type").equals(driverType)) {
                        System.setProperty(reader.getAttributeValue(null, "key"), reader.getAttributeValue(null, "filePath"));
                        String className = String.format("org.openqa.selenium.%s.%s", reader.getAttributeValue(null, "type")
                                .toLowerCase()
                                .replaceAll("driver", ""), reader.getAttributeValue(null, "type"));
                        Class<?> clazz = Class.forName(className);
                        WebDriver driver = (WebDriver) clazz.getConstructor().newInstance();
                        Map<String, WebDriver> driverMap = new HashMap<>();
                        driverMap.put(driverType, driver);
                        drivers.set(driverMap);
                    }
                }
            } catch (XMLStreamException | IOException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return drivers.get().get(driverType);
        }
    }

}
