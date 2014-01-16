package edu.revtek.updater;

import edu.revtek.concurrent.Instance;
import edu.revtek.util.ASMUtil;
import edu.revtek.util.IOUtil;
import edu.revtek.util.LocalClassLoader;
import edu.revtek.util.tree.basic.BasicTree;
import edu.revtek.util.tree.basic.BasicTreeNode;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.IntInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

/**
 * @author Caleb Bradford
 */
public class Updater extends Instance implements Runnable {

    public static void main(String[] args) {
        ThreadGroup group = new ThreadGroup("updater-thread-group");
        try {
            byte[] bytes = IOUtil.read(Updater.class.getResourceAsStream("/resources/deob.jar"));
            Updater updater = new Updater(group, new JarInputStream(new ByteArrayInputStream(bytes)));
            Thread t = updater.createThread(updater);
            t.start();
            t.join();
            BasicTree tree = new BasicTree(new BasicTreeNode<>("Revision #" + updater.revision));
            for (AbstractContainer container : updater.containers) {
                /*if (container.node != null) {
                    System.out.println(container.name() + " identified as " + container.node.name);
                    for (Map.Entry<String, Hook> entry : container.hooks.entrySet()) {
                        Hook hook = entry.getValue();
                        System.out.print('\t' + entry.getKey() + ' ');
                        System.out.println(hook == null ? "is broken" : ("identified as " + hook.owner + "." + hook.name));
                    }
                }*/
                tree.getRoot().add(container.branch());
            }
            tree.write(System.out);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Manifest manifest;
    private Map<String, byte[]> files = new HashMap<>();
    private Map<String, ClassNode> classNodes = new HashMap<>();
    private List<AbstractContainer> containers = new Vector<>();
    private int revision = -1;

    public Updater(ThreadGroup threadGroup, JarInputStream in) {
        super(threadGroup);
        manifest = in.getManifest();
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

    public Thread createThread(Runnable runnable) {
        return new Thread(getThreadGroup(), runnable);
    }

    public static Updater get() {
        return Instance.get(Thread.currentThread().getThreadGroup(), Updater.class);
    }

    public static Updater get(ThreadGroup group) {
        return Instance.get(group, Updater.class);
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
                IntInsnNode nextInt = nextInt(current);
                if (nextInt == null)
                    return -1;
                if (nextInt.operand != 765)
                    continue;
                if ((nextInt = nextInt(nextInt)).operand != 503)
                    continue;
                return nextInt(nextInt).operand;
            } catch (NullPointerException e) {
                throw new RuntimeException("Couldn't identify revision, no more instructions");
            }
        }
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

}
