package edu.revtek.util.asm;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.FieldNode;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;

/**
 * @author Caleb Bradford
 */
public class ASMUtil {

    public static ClassNode newClassNode(byte[] bytes) {
        ClassReader reader = new ClassReader(bytes);
        ClassNode cn = new ClassNode();
        reader.accept(cn, ClassReader.SKIP_DEBUG);
        return cn;
    }

    public static ClassNode newClassNode(InputStream in) {
        ClassReader reader = null;
        try {
            reader = new ClassReader(in);
            ClassNode cn = new ClassNode();
            reader.accept(cn, ClassReader.SKIP_DEBUG);
            return cn;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getFieldCount(boolean static_, ClassNode cn, String desc) {
        int count = 0;
        for (FieldNode fn : cn.fields) {
            boolean isStatic = Modifier.isStatic(fn.access);
            if (isStatic == static_ && fn.desc.equals(desc)) {
                count++;
            }
        }
        return count;
    }

}
