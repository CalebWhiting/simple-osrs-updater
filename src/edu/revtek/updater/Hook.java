package edu.revtek.updater;

import edu.revtek.lang.collections.tree.Branch;
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;

/**
 * @author Caleb Whiting
 *
 * A wrapper for holding data relevent to a hook value
 */
public class Hook {

    /**
     * The key for identifying this hook
     */
    private final String key;

    /**
     * The owner of this hook
     */
    private String owner;

    /**
     * The name of this hook
     */
    private String name;

    /**
     * The description of this hook
     */
    private String description;

    /**
     * Construct a new Hook object
     *
     * @param key the key for identifying this hook
     */
    public Hook(String key) {
        this.key = key;
    }

    /**
     * Gets the key of this hook
     *
     * @return the key of this hook
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the owner of this hook
     *
     * @return the owner of this hook
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Gets the name of this hook
     *
     * @return the name of this hook
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of this hook
     *
     * @return the description of this hook
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this hook to the given value
     *
     * @param description the value to set {@link #description}
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the location, name and description of this hook
     *
     * @param owner the class location of this hook
     * @param name  the name of this hook
     * @param desc  the description of this hook
     */
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

    /**
     * Sets the location, name and description of this hook
     *
     * @param fin the instruction to set this hook to
     */
    public void set(FieldInsnNode fin) {
        this.set(fin.owner, fin.name, fin.desc);
    }

    /**
     * Creates a branch with data relevant to this hook
     *
     * @return a branch with data relevant to this hook
     */
    public Branch<String> branch() {
        return new Branch<>(" @ " + key + " identified as '" + description + " " + owner + "." + name + "'");
    }

}
