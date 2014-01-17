package edu.revtek.updater.asm;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.FieldNode;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/**
 * @author Caleb Whiting
 *         <p/>
 *         Useful utilities for the ASM API
 */
public class ASMUtil {

    public static ClassNode newClassNode(byte[] bytes) {
        ClassReader reader = new ClassReader(bytes);
        ClassNode cn = new ClassNode();
        reader.accept(cn, ClassReader.SKIP_DEBUG);
        return cn;
    }

    public static ClassNode newClassNode(InputStream in) {
        try {
            ClassReader reader = new ClassReader(in);
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

    public static void write(String name, Map<String, ClassNode> classNodes) throws IOException {
        JarOutputStream out = new JarOutputStream(new FileOutputStream(name));
        for (ClassNode cn : classNodes.values()) {
            out.putNextEntry(new JarEntry(cn.name + ".class"));
            out.write(ASMUtil.getBytes(cn));
            out.closeEntry();
        }
        out.close();
    }

    private static byte[] getBytes(ClassNode cn) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(writer);
        return writer.toByteArray();
    }

    public static FieldNode getLocalField(String name, String desc, ClassNode cn) {
        for (FieldNode fn : cn.fields) {
            if ((name == null || fn.name.equals(name)) && (desc == null || fn.desc.equals(desc)))
                return fn;
        }
        return null;
    }

}
