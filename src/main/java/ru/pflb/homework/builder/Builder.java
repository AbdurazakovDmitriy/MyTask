package ru.pflb.homework.builder;

import org.apache.commons.io.FileUtils;
import ru.pflb.homework.CustomClassLoader;
import ru.pflb.homework.annotations.Element;
import ru.pflb.homework.elementModels.BasicElement;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class Builder {
    //driver types
    public static final String CHROME = "Chrome";
    public static final String FIREFOX = "Firefox";

    //page attributes
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String CHROME_PATH = "ChromePath";
    private static final String FIREFOX_PATH = "FirefoxPath";
    private static final String DEPRECATED = "deprecated";

    public static void main(String[] args) throws IOException, XMLStreamException, ClassNotFoundException, InterruptedException {
        String chromePath = parseDriverPath(CHROME);
        String firefoxPath = parseDriverPath(FIREFOX);
        System.out.println(chromePath);
        System.out.println(firefoxPath);


//        System.out.println(buildPage(CHROME, "StartPage").getSimpleName());
//        System.out.println(buildPage(CHROME, "AnotherPage").getSimpleName());
        destroyPages();
    }

    /**
     * Возвращает путь к указанному драйверу
     *
     * @param tagName имя драйвера
     * @return путь к драйверу
     * @throws XMLStreamException
     * @throws IOException
     */
    public static synchronized String parseDriverPath(String tagName) {
        try (StaxStreamProcessor processor = new StaxStreamProcessor(Files.newInputStream(Paths.get("./src/main/resources/PageXmlSources.xml")))) {
            XMLStreamReader reader = processor.getReader();
            while (reader.hasNext()) {       // while not end of XML
                int event = reader.next();   // read next event
                if (event == XMLEvent.START_ELEMENT &&
                    tagName.equals(reader.getLocalName())) {
                    return reader.getElementText();
                }
            }
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Возвращает список элементов указанной страницы
     *
     * @param pageName имя страницы
     * @return список элементов
     * @throws IOException
     * @throws XMLStreamException
     */
    public static synchronized List<BasicElement> parsePage(String pageName) {
        List<BasicElement> elementList = new ArrayList<>();
        try (StaxStreamProcessor processor = new StaxStreamProcessor(Files.newInputStream(Paths.get("./src/main/resources/PageXmlSources.xml")))) {
            XMLStreamReader reader = processor.getReader();
            while (reader.hasNext()) {       // while not end of XML
                int event = reader.next();   // read next event

                if (event == XMLEvent.START_ELEMENT && pageName.equals(reader.getAttributeValue(null, "name"))) {
                    int unclosedTags = 1;
                    while (reader.hasNext() && unclosedTags != 0) {
                        int elementEvent = reader.next();
                        //подсчет разницы между количеством открывающих и закрывающих тегов
                        if (reader.getEventType() == XMLEvent.START_ELEMENT) {
                            unclosedTags += 1;
                        } else if (reader.getEventType() == XMLEvent.END_ELEMENT) {
                            unclosedTags -= 1;
                        }

                        if (elementEvent == XMLEvent.START_ELEMENT) {
                            BasicElement basicElement = new BasicElement();
                            basicElement.setDeprecated(reader.getAttributeValue(null, DEPRECATED));
                            basicElement.setName(reader.getAttributeValue(null, NAME));
                            basicElement.setType(reader.getAttributeValue(null, TYPE));
                            basicElement.setChromePath(reader.getAttributeValue(null, CHROME_PATH));
                            basicElement.setFirefoxPath(reader.getAttributeValue(null, FIREFOX_PATH));
                            elementList.add(basicElement);
                        }
                    }
                }
            }
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
        }
        return elementList;
    }

    public static Class buildPage(String driverType, String pageName) {
        String basicPage = null;
        try {
            basicPage = FileUtils.readFileToString(new File("./src/main/resources/Page.java"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        basicPage = Objects.requireNonNull(basicPage).replaceFirst("@Page\\(\"(.*)\"\\)", "@Page\\(\"" + pageName + "\"\\)");
        basicPage = basicPage.replaceFirst("PageName", pageName);
        int indexOfSelectableElement = basicPage.indexOf('{');
        StringBuilder stringBuilder = new StringBuilder(basicPage);
        List<BasicElement> elementList = parsePage(pageName);

        String element = "public typeHolder elementNameHolder(){\n\r return DriverManager.get(DriverManager.Driver.of(\"driverHolder\")).findElement(By.xpath(\"pathHolder\"));\n\r}\n\r";
        for (BasicElement basicElement : elementList) {
            if (basicElement.isDeprecated())
                continue;
            stringBuilder.insert(indexOfSelectableElement + 1, element.replaceAll("elementNameHolder", basicElement.getName())
                .replaceAll("pathHolder", driverType.equals(CHROME) ? basicElement.getChromePath() : driverType.equals(FIREFOX) ? basicElement.getFirefoxPath() : "")
                .replaceAll("typeHolder", basicElement.getType())
                .replaceAll("elementNameHolder", basicElement.getName())
                .replaceAll("driverHolder",driverType));
        }



        String pageClassname = String.format("./src/main/java/ru/pflb/homework/page/%s.java", pageName);
        File pageFile = new File(pageClassname);
        try {
            FileUtils.write(pageFile, stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Properties properties = System.getProperties();
        String sep = properties.getProperty("file.separator");
        String jrePath = properties.getProperty("java.home");
        String classFileDirectory = String.format(".%starget%sclasses", sep, sep); //todo добавлен путь для файла .class
        Element.class.getPackageName();
        String javac = "\"" + jrePath + sep + "bin" + sep + "javac\"";
        String command = String.format("%s -d %s -classpath \"%s\" %s", javac, classFileDirectory, System.getProperty("java.class.path"), pageFile.getAbsolutePath());

        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Objects.requireNonNull(process).waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int pointIndex = pageFile.getAbsolutePath().lastIndexOf(".");
        String absolutePath = pageFile.getAbsolutePath().substring(0, pointIndex);
        CustomClassLoader customClassLoader = new CustomClassLoader();
        try {
            return customClassLoader.findClass(String.format("%s%sru%spflb%shomework%spage%s%s", classFileDirectory, sep, sep, sep, sep, sep, pageName)); //todo изменен путь
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Удаляет все созданные страницы
     *
     * @throws IOException
     * @throws XMLStreamException
     */
    public static void destroyPages() throws IOException, XMLStreamException {
        try (StaxStreamProcessor processor = new StaxStreamProcessor(Files.newInputStream(Paths.get("./src/main/resources/PageXmlSources.xml")))) {
            XMLStreamReader reader = processor.getReader();
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT) {
                    String tagName = reader.getLocalName();
                    if (tagName.equals("Page")) {
                        String pageName = reader.getAttributeValue(null, NAME);
                        File javaFile = new File(String.format("./src/main/java/ru/pflb/homework/page/%s.java", pageName));
                        javaFile.deleteOnExit();
                    }
                }
            }
        }
    }
}
