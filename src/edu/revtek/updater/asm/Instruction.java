package edu.revtek.updater.asm;

import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;
import jdk.internal.org.objectweb.asm.tree.TypeInsnNode;

/**
 * @author Caleb Whiting
 *         <p/>
 *         Represents a instruction identifier
 */
public abstract class Instruction {

    public abstract boolean accept(AbstractInsnNode node);

    public static Instruction getAbstractInsn(final int opcode, final Class type) {
        return new Instruction() {
            @Override
            public boolean accept(AbstractInsnNode node) {
                return (opcode == -1 | opcode == node.getOpcode()) && (type == null || type == node.getClass());
            }
        };
    }

    public static Instruction getAbstractInsn(final int opcode) {
        return getAbstractInsn(opcode, null);
    }

    public static Instruction getAbstractInsn(final Class type) {
        return getAbstractInsn(-1, type);
    }

    public static Instruction getFieldInsn(
            final int opcode,
            final String owner,
            final String name,
            final String desc) {
        return new Instruction() {
            @Override
            public boolean accept(AbstractInsnNode node) {
                if (node.getType() == AbstractInsnNode.FIELD_INSN) {
                    FieldInsnNode f = (FieldInsnNode) node;
                    return (opcode == -1 || opcode == node.getOpcode())
                            && (owner == null || owner.equals(f.owner))
                            && (name == null || name.equals(f.name))
                            && (desc == null || desc.equals(f.desc));
                }
                return false;
            }
        };
    }

    public static Instruction getTypeInsn(final int opcode, final String desc) {
        return new Instruction() {
            @Override
            public boolean accept(AbstractInsnNode node) {
                if (node.getType() == AbstractInsnNode.TYPE_INSN) {
                    TypeInsnNode t = (TypeInsnNode) node;
                    return (opcode == -1 || opcode == node.getOpcode())
                            && (desc == null || desc.equals(t.desc));
                }
                return false;
            }
        };
    }

}
