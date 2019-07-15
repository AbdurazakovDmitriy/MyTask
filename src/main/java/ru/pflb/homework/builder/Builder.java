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
import java.util.Properties;

public class Builder {
    //driver types
    private static final String CHROME = "Chrome";
    private static final String FIREFOX = "Firefox";

    //page attributes
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String CHROME_PATH = "ChromePath";
    private static final String FIREFOX_PATH = "FirefoxPath";

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
    public static synchronized String parseDriverPath(String tagName) throws XMLStreamException, IOException {
        try (StaxStreamProcessor processor = new StaxStreamProcessor(Files.newInputStream(Paths.get("./src/main/resources/PageXmlSources.xml")))) {
            XMLStreamReader reader = processor.getReader();
            while (reader.hasNext()) {       // while not end of XML
                int event = reader.next();   // read next event
                if (event == XMLEvent.START_ELEMENT &&
                    tagName.equals(reader.getLocalName())) {
                    return reader.getElementText();
                }
            }
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
    public static synchronized List<BasicElement> parsePage(String pageName) throws IOException, XMLStreamException {
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
                            basicElement.setName(reader.getAttributeValue(null, NAME));
                            basicElement.setType(reader.getAttributeValue(null, TYPE));
                            basicElement.setChromePath(reader.getAttributeValue(null, CHROME_PATH));
                            basicElement.setFirefoxPath(reader.getAttributeValue(null, FIREFOX_PATH));
                            elementList.add(basicElement);
                        }
                    }
                }
            }
        }
        return elementList;
    }

    public static Class buildPage(String driverType, String pageName) throws IOException, XMLStreamException, ClassNotFoundException, InterruptedException {
        String basicPage = FileUtils.readFileToString(new File("./src/main/resources/Page.java"));
        basicPage = basicPage.replaceFirst("@Page\\(\"(.*)\"\\)", "@Page\\(\"" + pageName + "\"\\)");
        basicPage = basicPage.replaceFirst("PageName", pageName);
        int indexOfSelectableElement = basicPage.indexOf('{');
        StringBuilder stringBuilder = new StringBuilder(basicPage);
        List<BasicElement> elementList = parsePage(pageName);
        String element = "\n\r@Element(\"elementNameHolder\")\n\r@FindBy(xpath=\"pathHolder\")\n\rprivate typeHolder elementNameHolder;";
        for (BasicElement basicElement : elementList) {
            stringBuilder.insert(indexOfSelectableElement + 1, element.replaceAll("elementNameHolder", basicElement.getName())
                .replaceAll("pathHolder", driverType.equals(CHROME) ? basicElement.getChromePath() : driverType.equals(FIREFOX) ? basicElement.getFirefoxPath() : "")
                .replaceAll("typeHolder", basicElement.getType())
                .replaceAll("elementNameHolder", basicElement.getName()));
        }
        String pageClassname = String.format("./src/main/java/ru/pflb/homework/page/%s.java", pageName);
        File pageFile = new File(pageClassname);
        FileUtils.write(pageFile, stringBuilder.toString());


        Properties properties = System.getProperties();
        String sep = properties.getProperty("file.separator");
        String jrePath = properties.getProperty("java.home");
        String classFileDirectory = String.format(".%starget%sclasses", sep, sep); //todo добавлен путь для файла .class
        Element.class.getPackageName();
        String javac = "\"" + jrePath + sep + "bin" + sep + "javac\"";
        String command = String.format("%s -d %s -classpath \"%s\" %s", javac, classFileDirectory, System.getProperty("java.class.path"), pageFile.getAbsolutePath());

        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();

        int pointIndex = pageFile.getAbsolutePath().lastIndexOf(".");
        String absolutePath = pageFile.getAbsolutePath().substring(0, pointIndex);
        CustomClassLoader customClassLoader = new CustomClassLoader();
        return customClassLoader.findClass(String.format("%s%sru%spflb%shomework%spage%s%s", classFileDirectory, sep, sep, sep, sep, sep, pageName)); //todo изменен путь
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
