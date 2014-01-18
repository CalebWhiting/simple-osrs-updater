package edu.revtek.updater.container;

import edu.revtek.updater.AbstractContainer;
import edu.revtek.updater.Updater;
import edu.revtek.updater.asm.ASMUtil;
import edu.revtek.updater.asm.Instruction;
import edu.revtek.updater.asm.InstructionPattern;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.Collection;
import java.util.List;

/**
 * @author Caleb Whiting
 */
public class CanvasContainer extends AbstractContainer {

    @Override
    public void initKeys(List<String> keys) {
        keys.add("component");
    }

    @Override
    public void initDependencies(Collection<Class> dependencies) {

    }

    @Override
    public boolean accept(ClassNode cn) {
        return cn.superName.equals("java/awt/Canvas");
    }

    @Override
    public void visit(ClassNode cn) {
        FieldNode component = ASMUtil.getLocalField(null, "Ljava/awt/Component;", cn);
        hooks.get("component").set(cn.name, component.name, component.desc);
        getCanvas();
    }

    private void getCanvas() {
        InstructionPattern pattern = new InstructionPattern(
                Instruction.getTypeInsn(Opcodes.NEW, node.name),
                Instruction.getFieldInsn(Opcodes.PUTSTATIC, null, null, "Ljava/awt/Canvas;")
        );
        ClientContainer client = Updater.get().getContainer(ClientContainer.class);
        for (ClassNode cn : Updater.get().getClassNodes().values()) {
            for (MethodNode mn : cn.methods) {
                AbstractInsnNode[] nodes = pattern.find(mn);
                if (nodes == null) continue;
                client.hooks.get("canvas").set((FieldInsnNode) nodes[1]);
            }
        }
    }

}
