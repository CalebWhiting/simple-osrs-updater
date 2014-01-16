package edu.revtek.updater.container;

import edu.revtek.updater.AbstractContainer;
import edu.revtek.updater.Hook;
import edu.revtek.util.ASMUtil;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.FieldNode;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

/**
 * @author Caleb Bradford
 */
public class NodeContainer extends AbstractContainer {

    @Override
    public void initKeys(List<String> keys) {
        keys.add("uid");
        keys.add("next");
        keys.add("previous");
    }

    @Override
    public void initDependencies(Collection<Class> dependencies) {

    }

    @Override
    public boolean accept(ClassNode cn) {
        if (cn.superName.equals("java/lang/Object")) {
            int selfCount = ASMUtil.getFieldCount(false, cn, "L" + cn.name + ";");
            long longCount = ASMUtil.getFieldCount(false, cn, "J");
            return selfCount == 2 && longCount == 1;
        }
        return false;
    }

    @Override
    public void visit(ClassNode cn) {
        for (FieldNode f : cn.fields) {
            if (!Modifier.isStatic(f.access) && f.desc.equals("J"))
                hooks.put("uid", new Hook(cn.name, f.name, "J"));
        }
    }

}
