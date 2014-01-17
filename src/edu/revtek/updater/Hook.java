package edu.revtek.updater;

import edu.revtek.util.tree.basic.BasicTreeNode;
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Caleb Bradford
 *
 * A wrapper for holding data relevent to a hook value
 */
public class Hook {

    private List<String> errors = new LinkedList<>();

    private final String key;

    private String owner;
    private String name;
    private String description;

    public Hook(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void set(String owner, String name, String desc) {
        if (this.owner != null && this.name != null) {
            if (owner.equals(this.owner) && name.equals(this.name)) {
                return;
            }
            System.out.printf("%s is already set to %s.%s (trying to set to %s.%s)%n",
                    key, this.owner, this.name, owner, name);
            return;
        }
        this.owner = owner;
        this.name = name;
        this.description = desc;
    }

    public void set(FieldInsnNode fin) {
        this.set(fin.owner, fin.name, fin.desc);
    }

    public BasicTreeNode<String> branch(String key) {
        return new BasicTreeNode<>(key + " identified as '" + description + " " + owner + "." + name + "'");
    }

}
