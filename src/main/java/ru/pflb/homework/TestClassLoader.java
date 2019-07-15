package ru.pflb.homework;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

public class TestClassLoader {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        File file = new File("./src/main/resources/TargetClass.java");
        Properties properties = System.getProperties();
        String sep = properties.getProperty("file.separator");
        String jrePath = properties.getProperty("java.home");
        String javac =  jrePath + sep + "bin" + sep + "javac" + ".exe";
        System.out.println(javac);
        File javacompiler = new File(javac);
        Process process = Runtime.getRuntime().exec(javac + " " + file.getAbsolutePath());
        System.out.println(process.waitFor());




        int pointIndex = file.getAbsolutePath().lastIndexOf(".");
        String absolutePath = file.getAbsolutePath().substring(0, pointIndex);
        CustomClassLoader customClassLoader = new CustomClassLoader();
        Class aClass =  customClassLoader.findClass(absolutePath);
        Method method = aClass.getMethod("print", String.class);
        method.invoke(aClass.newInstance(), "Hello World");

        
    }
}
