package edu.revtek.updater.asm.instructions;

import edu.revtek.updater.asm.Instruction;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;

/**
 * @author Caleb Whiting
 */
public class AbstractInstruction implements Instruction {

    private int opcode = -1;
    private Class type = null;

    public AbstractInstruction(int opcode) {
        this.opcode = opcode;
    }

    public AbstractInstruction(Class type) {
        this.type = type;
    }

    @Override
    public boolean accept(AbstractInsnNode node) {
        return (this.opcode == -1 || opcode == node.getOpcode()) &&
                (this.type == null || type == node.getClass());
    }

}
