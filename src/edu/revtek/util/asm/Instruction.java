package edu.revtek.util.asm;

import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;

/**
 * @author Caleb Bradford
 */
public interface Instruction {

    public boolean accept (AbstractInsnNode node);

}
