package edu.revtek.updater.container;

import edu.revtek.updater.AbstractContainer;
import edu.revtek.updater.asm.ASMUtil;
import edu.revtek.updater.asm.InstructionPattern;
import edu.revtek.updater.asm.instructions.AbstractInstruction;
import edu.revtek.updater.asm.instructions.FieldInstruction;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;
import jdk.internal.org.objectweb.asm.tree.FieldNode;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

/**
 * @author Caleb Whiting
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
                hooks.get("uid").set(cn.name, f.name, "J");
        }
        addHooks(this, cn);
    }

    protected static void addHooks(AbstractContainer container, ClassNode c) {
        InstructionPattern pattern = new InstructionPattern(12,
                new AbstractInstruction(Opcodes.ACONST_NULL),
                new FieldInstruction(Opcodes.PUTFIELD, c.name, null, "L" + c.name + ";"),
                new FieldInstruction(Opcodes.PUTFIELD, c.name, null, "L" + c.name + ";")
        );
        AbstractInsnNode[] nodes = pattern.find(c);
        if (nodes != null) {
            container.hooks.get("next").set((FieldInsnNode) nodes[1]);
            container.hooks.get("previous").set((FieldInsnNode) nodes[2]);
        }
    }

}
