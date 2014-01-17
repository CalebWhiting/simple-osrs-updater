package edu.revtek.util.asm;

import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;

/**
 * @author Caleb Bradford
 *
 * Represents a instruction identifier
 */
public interface Instruction {

    public boolean accept (AbstractInsnNode node);

}
