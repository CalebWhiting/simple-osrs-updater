package edu.revtek.updater;

import edu.revtek.lang.concurrent.Instance;
import edu.revtek.lang.util.IOUtil;
import edu.revtek.lang.util.LocalClassLoader;
import edu.revtek.updater.asm.ASMUtil;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.IntInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

/**
 * @author Caleb Whiting
 *         <p/>
 *         A class representing an archive identifier
 */
public class Updater extends Instance implements Runnable {

    private Manifest manifest;
    private Map<String, byte[]> files = new HashMap<>();
    private Map<String, ClassNode> classNodes = new HashMap<>();
    private List<AbstractContainer> containers = new Vector<>();
    private int revision = -1;

    public Updater(ThreadGroup threadGroup, JarInputStream in) {
        super(threadGroup);
        this.manifest = in.getManifest();
        JarEntry entry;
        try {
            while ((entry = in.getNextJarEntry()) != null) {
                byte[] bytes = IOUtil.read(in);
                if (entry.getName().endsWith(".class")) {
                    ClassNode cn = ASMUtil.newClassNode(bytes);
                    classNodes.put(cn.name, cn);
                } else {
                    files.put(entry.getName(), bytes);
                }
            }
            Class[] classes = LocalClassLoader.getClassesIn("edu.revtek.updater.container", true);
            for (Class c : classes) {
                if (AbstractContainer.class.isAssignableFrom(c)) {
                    containers.add((AbstractContainer) c.newInstance());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.revision = getRevision(classNodes.get("client"));
        List<AbstractContainer> containers = new Vector<>();
        for (AbstractContainer container : this.containers) {
            containers.add(container);
        }
        List<Class> finished = new Vector<>();
        int size;
        while ((size = containers.size()) > 0) {
            for (AbstractContainer container : containers) {
                if (container.dependencies.length == 0 ||
                        finished.containsAll(Arrays.asList(container.dependencies))) {
                    // run container
                    List<ClassNode> accepted = new Vector<>();
                    for (ClassNode cn : classNodes.values()) {
                        if (container.accept(cn))
                            accepted.add(cn);
                    }
                    if (accepted.size() == 1) {
                        ClassNode cn = accepted.get(0);
                        container.node = cn;
                        container.visit(cn);
                        finished.add(container.getClass());
                    } else {
                        if (accepted.size() == 0) {
                            throw new RuntimeException("container " + container.getClass() + " didn't accept any ClassNode's");
                        }
                        List<String> names = new Vector<>();
                        for (ClassNode cn : accepted)
                            names.add(cn.name);
                        throw new RuntimeException("container " + container.getClass() + " accepted multiple ClassNode's " + names);
                    }
                    containers.remove(container);
                    break;
                }
            }
            if (containers.size() == size) {
                // nothing happened
                throw new RuntimeException("Couldn't run all containers");
            }
        }
    }

    private int getRevision(ClassNode client) {
        MethodNode init = null;
        for (MethodNode mn : client.methods) {
            if (mn.name.equals("init"))
                init = mn;
        }
        if (init == null)
            throw new RuntimeException("init() is null, this shouldn't happen!");
        AbstractInsnNode current = init.instructions.getFirst();
        while (true) {
            try {
                current = current.getNext();
                IntInsnNode nextInt;
                if ((nextInt = nextInt(current)).operand != 765)
                    continue;
                if ((nextInt = nextInt(nextInt)).operand != 503)
                    continue;
                return nextInt(nextInt).operand;
            } catch (NullPointerException e) {
                throw new RuntimeException("Couldn't identify revision, no more instructions");
            }
        }
    }

    public static Updater get(ThreadGroup group) {
        return Instance.get(group, Updater.class);
    }

    public static Updater get() {
        return get(Thread.currentThread().getThreadGroup());
    }

    private IntInsnNode nextInt(AbstractInsnNode node) {
        if (node == null) return null;
        AbstractInsnNode current = node;
        while ((current = current.getNext()) != null) {
            if (current.getOpcode() == Opcodes.SIPUSH || current.getOpcode() == Opcodes.BIPUSH)
                return ((IntInsnNode) current);
        }
        return null;
    }

    public <T extends AbstractContainer> T getContainer(Class<? extends T> type) {
        for (AbstractContainer container : containers) {
            if (container.getClass() == type) {
                //noinspection unchecked
                return (T) container;
            }
        }
        return null;
    }

    public Manifest getManifest() {
        return manifest;
    }

    public Map<String, byte[]> getFiles() {
        return files;
    }

    public Map<String, ClassNode> getClassNodes() {
        return classNodes;
    }

    public List<AbstractContainer> getContainers() {
        return containers;
    }

    public int getRevision() {
        return revision;
    }
}
