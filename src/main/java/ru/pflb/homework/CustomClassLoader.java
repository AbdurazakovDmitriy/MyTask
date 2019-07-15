package ru.pflb.homework;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class CustomClassLoader extends ClassLoader {
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        //D:\workDirectory\newBuild\frameWork\target\classes\ru\pflb\homework\page\StartPage.class
        File f = new File(name+".class");
        if(!f.isFile())
            throw new ClassNotFoundException("Нет такого класса " + name);
        InputStream ins = null;
        try{
            ins = new BufferedInputStream(new FileInputStream(f));
            byte[]b = new byte[(int)f.length()];
            int length = ins.read(b);
            return defineClass(null, b, 0, length);
        }catch (Exception e){
            e.printStackTrace();
            throw new ClassNotFoundException("Проблемы с байт кодом");
        }
        finally {
            try {
                Objects.requireNonNull(ins).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
