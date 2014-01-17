package edu.revtek.updater.container;

import edu.revtek.updater.AbstractContainer;
import edu.revtek.updater.Updater;
import edu.revtek.util.asm.ASMUtil;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.util.Collection;
import java.util.List;

/**
 * @author Caleb Whiting
 */
public class CacheableContainer extends AbstractContainer {

    @Override
    public void initKeys(List<String> keys) {
        keys.add("next");
        keys.add("previous");
    }

    @Override
    public void initDependencies(Collection<Class> dependencies) {
        // we need to check if the superclass is the Node class
        // so we depend on NodeContainer
        dependencies.add(NodeContainer.class);
    }

    @Override
    public boolean accept(ClassNode cn) {
        NodeContainer node = Updater.get().getContainer(NodeContainer.class);
        if (cn.superName.equals(node.node.name)) {
            int selfCount = ASMUtil.getFieldCount(false, cn, "L" + cn.name + ";");
            return selfCount == 2;
        }
        return false;
    }

    @Override
    public void visit(ClassNode cn) {
        /**
         * Cacheable.next/previous has the same pattern as Node.next/previous
         */
        NodeContainer.addHooks(this, cn);
    }

}
