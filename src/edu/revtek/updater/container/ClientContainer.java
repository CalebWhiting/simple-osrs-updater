package edu.revtek.updater.container;

import edu.revtek.updater.AbstractContainer;
import edu.revtek.updater.Updater;
import edu.revtek.updater.asm.InstructionPattern;
import edu.revtek.updater.asm.instructions.AbstractInstruction;
import edu.revtek.updater.asm.instructions.FieldInstruction;
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
        /*
        if (var == 0) {
            return var;
        }
        if (var == 1) {
            var = level_array[var[var++]];
        }
        if (var == 2) {
            var = real_level_array[var[var++]];
        }
        if (var == 3) {
            var = exp_array[var[var++]];
        }
        */
        InstructionPattern pattern = new InstructionPattern(20,
                new AbstractInstruction(Opcodes.ICONST_0),
                new AbstractInstruction(Opcodes.IRETURN),
                new AbstractInstruction(Opcodes.ICONST_1),
                new FieldInstruction(Opcodes.GETSTATIC, null, null, "[I"),
                new AbstractInstruction(Opcodes.ICONST_2),
                new FieldInstruction(Opcodes.GETSTATIC, null, null, "[I"),
                new AbstractInstruction(Opcodes.ICONST_3),
                new FieldInstruction(Opcodes.GETSTATIC, null, null, "[I")

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
