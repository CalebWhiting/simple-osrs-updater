package edu.revtek.util.asm;
import edu.revtek.updater.Updater;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.InsnList;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.util.*;

/**
 * @author Caleb Bradford
 */
public class InstructionPattern {

    private Map<Object, AbstractInsnNode[]> startCache = new HashMap<>();

    private int spacing;
    private final Instruction[] instructions;

    public InstructionPattern(int spacing, Instruction... instructions) {
        this.spacing = spacing;
        this.instructions = instructions;
    }

    public InstructionPattern(Instruction... instructions) {
        this(12, instructions);
    }

    private AbstractInsnNode[] find(AbstractInsnNode[] starts) {
        for (AbstractInsnNode node : starts) {
            AbstractInsnNode[] located = getLocatedAt(node);
            if (located != null) {
                return located;
            }
        }
        return null;
    }

    public AbstractInsnNode[] find(MethodNode mn) {
        return find(getStarts(mn));
    }

    public AbstractInsnNode[] find(ClassNode c) {
        return find(c.methods);
    }

    public AbstractInsnNode[] find(Collection<MethodNode> methods) {
        for (MethodNode m : methods) {
            AbstractInsnNode[] nodes = find(m);
            if (nodes == null)
                continue;
            return nodes;
        }
        return null;
    }

    public AbstractInsnNode[] findWithin(Collection<ClassNode> classNodes) {
        return find(getStarts(classNodes));
    }

    private AbstractInsnNode[] getLocatedAt(AbstractInsnNode start) {
        AbstractInsnNode current = start;
        List<AbstractInsnNode> located = new LinkedList<>();
        int i = 0;
        int passed = 0;
        while (current != null) {
            Instruction instruction = instructions[i];
            if (instruction.accept(current)) {
                passed = 0;
                i++;
                located.add(current);
                if (i >= instructions.length)
                    return located.toArray(new AbstractInsnNode[located.size()]);
            } else {
                if (passed >= spacing)
                    return null;
                passed++;
            }
            current = current.getNext();
        }
        return null;
    }

    public AbstractInsnNode[] getStarts(Collection<ClassNode> nodes) {
        AbstractInsnNode[] cacheValues = startCache.get(this);
        if (cacheValues != null) return cacheValues;
        List<AbstractInsnNode> starts = new Vector<>();
        for (ClassNode cn : nodes) {
            for (MethodNode mn : cn.methods)
                Collections.addAll(starts, getStarts(mn));
        }
        AbstractInsnNode[] instructions = starts.toArray(new AbstractInsnNode[starts.size()]);
        startCache.put(this, instructions);
        return instructions;
    }

    private AbstractInsnNode[] getStarts(MethodNode mn) {
        AbstractInsnNode[] cacheValues = startCache.get(mn);
        if (cacheValues != null) return cacheValues;
        List<AbstractInsnNode> starts = new Vector<>();
        Instruction start = instructions[0];
        InsnList i = mn.instructions;
        for (int index = 0; index < i.size(); index++) {
            AbstractInsnNode insn = i.get(index);
            if (start.accept(insn)) {
                starts.add(insn);
            }
        }
        AbstractInsnNode[] nodes = starts.toArray(new AbstractInsnNode[starts.size()]);
        startCache.put(mn, nodes);
        return nodes;
    }

}
