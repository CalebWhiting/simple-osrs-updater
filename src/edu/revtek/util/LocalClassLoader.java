package edu.revtek.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * @author Caleb Bradford
 */
public class LocalClassLoader {

    public static Class[] getClassesIn(String _package, boolean recursive) throws URISyntaxException, ClassNotFoundException {
        List<Class> classes = new Vector<>();
        String s = (_package.replace('.', '/'));
        if (!s.startsWith("/"))
            s = "/" + s;
        if (!s.endsWith("/"))
            s += "/";
        URL res = LocalClassLoader.class.getResource(s);
        if (res.getFile().contains("jar!/")) {
            getClassesInJar(classes, res, s);
        } else {
            File dir = new File(res.toURI());
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile()) {
                        if (f.getName().endsWith(".class")) {
                            classes.add(Class.forName(_package + '.' + f.getName().split(".class")[0]));
                        }
                    } else if (recursive) {
                        Collections.addAll(classes, getClassesIn(_package + '.' + f.getName(), true));
                    }
                }
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

    private static void getClassesInJar(List<Class> classes, URL res, String s) {
        String path = res.getPath();
        String jPath = path.substring(5, path.indexOf("!/"));
        try {
            JarInputStream in = new JarInputStream(new FileInputStream(jPath));
            JarEntry entry;
            while ((entry = in.getNextJarEntry()) != null) {
                if (entry.getName().startsWith(s.substring(1)) && entry.getName().endsWith(".class")) {
                    String className = (entry.getName().split(".class")[0]).replace('/', '.');
                    classes.add(Class.forName(className));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
