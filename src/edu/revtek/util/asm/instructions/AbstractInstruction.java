package edu.revtek.util.asm.instructions;

import edu.revtek.util.asm.Instruction;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;

/**
 * @author Caleb Bradford
 */
public class AbstractInstruction implements Instruction {

    private int constructor;
    private Integer opcode = null;
    private Class type = null;

    public AbstractInstruction(int opcode) {
        constructor = 0;
        this.opcode = opcode;
    }

    public AbstractInstruction(Class type) {
        constructor = 1;
        this.type = type;
    }

    @Override
    public boolean accept(AbstractInsnNode node) {
        switch (this.constructor) {
            case 0: // opcode
                return this.opcode == node.getOpcode();
            case 1: // type
                return type.isInstance(node);
            default:
                throw new RuntimeException("invalid constructor: " + this.constructor);
        }
    }

}
