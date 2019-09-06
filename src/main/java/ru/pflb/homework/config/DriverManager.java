package ru.pflb.homework.config;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.service.DriverService;
import ru.pflb.homework.builder.StaxStreamProcessor;
import ru.pflb.homework.utils.CustomLogger;
import ru.pflb.homework.utils.CustomReflection;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
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


    public static synchronized WebDriver getDW(String driverType) throws IOException, XMLStreamException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (driverType != null && !driverType.equals("") && drivers.get() != null && drivers.get().containsKey(driverType)) {
            return drivers.get().get(driverType);
        } else {
            try (StaxStreamProcessor processor = new StaxStreamProcessor(Files.newInputStream(Paths.get("./src/main/resources/PageXmlSources.xml")))) { //todo забуфферизировать файл
                XMLStreamReader reader = processor.getReader();
                while (reader.hasNext()) {
                    int event = reader.next();
                    if (event == XMLEvent.START_ELEMENT && reader.getLocalName().equals("Driver") && reader.getAttributeValue(null, "type").equals(driverType)) {
                        String driverTypeName = reader.getAttributeValue(null, "type");
                        String shortDriverType = driverTypeName.substring(0, driverTypeName.indexOf("Driver"));
                        String driverOptionsName = "org.openqa.selenium." + shortDriverType.toLowerCase() + "." + shortDriverType + "Options";
                        Class<?> driverOptionsClass = Class.forName(driverOptionsName);
//                        Object driverOptions = driverOptionsClass.getDeclaredConstructor().newInstance();
                        Object driverOptions = CustomReflection.createNewInstanceOr(driverOptionsClass,null);
                        CustomLogger.debug(String.format("Created driverOptions '%s'", driverOptionsName));//ChromeOptions или FireFoxOptions
                        //todo
                        String driverServiceName = "org.openqa.selenium." + shortDriverType.toLowerCase() + "." + reader.getAttributeValue(null, "serviceType");
                        Class<?> driverServiceClass = Class.forName(driverServiceName);

                        Method[] methods = driverServiceClass.getClasses()[1].getDeclaredMethods();
                        Method usingDriverExecutable = Arrays.stream(methods).filter(o -> o.getName().equals("usingDriverExecutable")).findFirst().get();

                        Object driverBuilder = driverServiceClass.getDeclaredClasses()[0].getDeclaredConstructor().newInstance();
                        usingDriverExecutable.setAccessible(true);
                        usingDriverExecutable.invoke(driverBuilder, new File(reader.getAttributeValue(null, "filePath")));
                        Object driverService = ((DriverService.Builder) driverBuilder).build();
                        CustomLogger.debug(String.format("Created driverService '%s'",driverServiceName));

                        Method[] driverOptionsMethods = driverOptionsClass.getDeclaredMethods();
                        Arrays.stream(driverOptionsMethods).filter(o -> o.getName().equals("setPageLoadStrategy")).findFirst().get().invoke(driverOptions, PageLoadStrategy.NORMAL);
//                        Arrays.stream(driverOptionsMethods).filter(o -> o.getName().equals("addArguments")).findFirst().get().invoke(driverOptions, "--start-maximized");

                        String className = String.format("org.openqa.selenium.%s.%s", reader.getAttributeValue(null, "type")
                            .toLowerCase()
                            .replaceAll("driver", ""), reader.getAttributeValue(null, "type"));
                        Class<?> clazz = Class.forName(className);
                        WebDriver driver = (WebDriver) clazz.getDeclaredConstructors()[0].newInstance(driverService, driverOptions);
                        Map<String, WebDriver> driverMap = new HashMap<>();
                        driverMap.put(driverType, driver);
                        drivers.set(driverMap);
                    }
                }
            }
        }
        return drivers.get().get(driverType);
    }

}
