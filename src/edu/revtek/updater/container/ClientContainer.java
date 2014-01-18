package edu.revtek.updater.container;

import edu.revtek.updater.AbstractContainer;
import edu.revtek.updater.Updater;
import edu.revtek.updater.asm.Instruction;
import edu.revtek.updater.asm.InstructionPattern;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

/**
 * @author Caleb Whiting
 */
public class ClientContainer extends AbstractContainer {

    @Override
    public void initKeys(List<String> keys) {
        keys.add("levels");
        keys.add("realLevels");
        keys.add("experiences");
        keys.add("canvas");
        keys.add("mouse");
        keys.add("keyboard");
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
        getSkillData();
    }

    private void getSkillData() {
        InstructionPattern pattern = new InstructionPattern(20,
                Instruction.getAbstractInsn(Opcodes.ICONST_0),
                Instruction.getAbstractInsn(Opcodes.IRETURN),
                Instruction.getAbstractInsn(Opcodes.ICONST_1),
                Instruction.getFieldInsn(Opcodes.GETSTATIC, null, null, "[I"),
                Instruction.getAbstractInsn(Opcodes.ICONST_2),
                Instruction.getFieldInsn(Opcodes.GETSTATIC, null, null, "[I"),
                Instruction.getAbstractInsn(Opcodes.ICONST_3),
                Instruction.getFieldInsn(Opcodes.GETSTATIC, null, null, "[I")

        );
        for (ClassNode cn : Updater.get().getClassNodes().values()) {
            for (MethodNode mn : cn.methods) {
                AbstractInsnNode[] nodes = pattern.find(mn);
                if (nodes == null)
                    continue;
                hooks.get("levels").set((FieldInsnNode) nodes[3]);
                hooks.get("realLevels").set((FieldInsnNode) nodes[5]);
                hooks.get("experiences").set((FieldInsnNode) nodes[7]);
            }
        }
    }

    public void lazyHook(String key, String desc) {
        for (ClassNode cn : Updater.get().getClassNodes().values()) {
            for (FieldNode fn : cn.fields) {
                if (Modifier.isStatic(fn.access) && fn.desc.equals(desc))
                    hooks.get(key).set(cn.name, fn.name, fn.desc);
            }
        }
    }

}
