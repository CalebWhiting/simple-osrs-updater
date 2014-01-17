package edu.revtek.updater.asm;

import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;

/**
 * @author Caleb Whiting
 *         <p/>
 *         Represents a instruction identifier
 */
public interface Instruction {

    public boolean accept(AbstractInsnNode node);

}
