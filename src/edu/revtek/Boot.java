package edu.revtek;

import edu.revtek.concurrent.Instance;
import edu.revtek.updater.AbstractContainer;
import edu.revtek.updater.Updater;
import edu.revtek.util.IOUtil;
import edu.revtek.util.tree.basic.BasicTree;
import edu.revtek.util.tree.basic.BasicTreeNode;
import edu.revtek.util.tree.basic.EmptyBasicTreeNode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.jar.JarInputStream;

/**
 * @author Caleb Whiting
 */
public class Boot {

    public static void main(String[] args) {
        // the ThreadGroup this updater will be running on
        ThreadGroup group = new ThreadGroup("updater-thread-group");
        try {
            byte[] bytes = IOUtil.read(Updater.class.getResourceAsStream("/resources/deob.jar"));
            Updater updater = new Updater(group, new JarInputStream(new ByteArrayInputStream(bytes)));
            // run updater
            Instance.invoke(updater);
            Thread t = new Thread(updater.getThreadGroup(), updater);
            t.start();
            t.join();
            // build output tree
            BasicTree tree = new BasicTree(new BasicTreeNode<>("Revision #" + updater.getRevision()));
            for (AbstractContainer container : updater.getContainers()) {
                tree.getRoot().add(new EmptyBasicTreeNode()); // separate classes with an empty line
                tree.getRoot().add(container.branch());
            }
            // print log
            tree.write(System.out);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
