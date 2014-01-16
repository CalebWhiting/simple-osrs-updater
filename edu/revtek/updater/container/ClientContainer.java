package edu.revtek.updater.container;

import edu.revtek.updater.AbstractContainer;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.util.Collection;
import java.util.List;

/**
 * @author Caleb Bradford
 */
public class ClientContainer extends AbstractContainer {

    @Override
    public void initKeys(List<String> keys) {

    }

    @Override
    public void initDependencies(Collection<Class> dependencies) {

    }

    @Override
    public boolean accept(ClassNode cn) {
        return cn.name.equals("client");
    }

    @Override
    public void visit(ClassNode cn) {

    }

}
