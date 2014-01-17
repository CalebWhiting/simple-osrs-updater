package edu.revtek.util.asm.instructions;

import edu.revtek.util.asm.Instruction;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;

/**
 * @author Caleb Whiting
 */
public class FieldInstruction implements Instruction {

    private int opcode;
    private String name;
    private String desc;
    private String owner;

    public FieldInstruction(int opcode, String owner, String name, String desc) {
        this.opcode = opcode;
        this.owner = owner;
        this.name = name;
        this.desc = desc;
    }

    public FieldInstruction(int opcode) {
        this(opcode, null, null, null);
    }

    public static FieldInstruction withOwner(String owner) {
        return new FieldInstruction(-1, owner, null, null);
    }

    public static FieldInstruction withName(String name) {
        return new FieldInstruction(-1, null, name, null);
    }

    public static FieldInstruction withDesc(String desc) {
        return new FieldInstruction(-1, null, null, desc);
    }

    @Override
    public boolean accept(AbstractInsnNode node) {
        if (node.getType() == AbstractInsnNode.FIELD_INSN) {
            FieldInsnNode fn = (FieldInsnNode) node;
            boolean opcode = this.opcode == -1 || this.opcode == node.getOpcode();
            boolean owner = this.owner == null || this.owner.equals(fn.owner);
            boolean name = this.name == null || this.name.equals(fn.name);
            boolean desc = this.desc == null || this.desc.equals(fn.desc);
            return opcode && owner && name && desc;
        }
        return false;
    }

}
