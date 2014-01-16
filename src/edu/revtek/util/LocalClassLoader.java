package edu.revtek.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

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
        return classes.toArray(new Class[classes.size()]);
    }

}
