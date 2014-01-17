package edu.revtek.updater;

import edu.revtek.lang.collections.tree.Branch;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.util.*;

/**
 * @author Caleb Whiting
 *         <p/>
 *         A abstract class representing a class identifier
 */
public abstract class AbstractContainer {

    /**
     * The list of hooks relevant to this container
     */
    public Map<String, Hook> hooks;

    /**
     * The {@link jdk.internal.org.objectweb.asm.tree.ClassNode} that was accepted
     */
    public ClassNode node;

    /**
     * The hooks this container is expected to retrieve
     */
    String[] keys;

    /**
     * The dependencies this container must wait for before running
     */
    Class[] dependencies;

    {
        // init container
        List<String> keys = new LinkedList<>();
        initKeys(keys);
        List<Class> dependencies = new Vector<>();
        initDependencies(dependencies);
        this.keys = keys.toArray(new String[keys.size()]);
        this.dependencies = dependencies.toArray(new Class[dependencies.size()]);
        this.hooks = new LinkedHashMap<>();
        // add keys to the hooks list as placeholders
        for (String s : this.keys) {
            this.hooks.put(s, new Hook(s));
        }
    }

    /**
     * Sets the list of keys
     *
     * @param keys The list to add the keys to
     * @see edu.revtek.updater.AbstractContainer#keys
     */
    public abstract void initKeys(List<String> keys);

    /**
     * Sets the list of dependencies
     *
     * @param dependencies The list to add the dependencies to
     * @see edu.revtek.updater.AbstractContainer#dependencies
     */
    public abstract void initDependencies(Collection<Class> dependencies);

    /**
     * Check if the given {@link jdk.internal.org.objectweb.asm.tree.ClassNode} is acceptable
     *
     * @param cn The {@link jdk.internal.org.objectweb.asm.tree.ClassNode} to check
     * @return True if the given {@link jdk.internal.org.objectweb.asm.tree.ClassNode} is acceptable; otherwise false
     */
    public abstract boolean accept(ClassNode cn);

    /**
     * Visit the given {@link jdk.internal.org.objectweb.asm.tree.ClassNode} to identify relevant data
     *
     * @param cn The {@link jdk.internal.org.objectweb.asm.tree.ClassNode} to visit
     */
    public abstract void visit(ClassNode cn);

    /**
     * Gets the keys of this container
     *
     * @return {@link edu.revtek.updater.AbstractContainer#keys}
     */
    public String[] keys() {
        return keys;
    }

    /**
     * Gets the dependencies of this container
     *
     * @return {@link edu.revtek.updater.AbstractContainer#dependencies}
     */
    public Class[] dependencies() {
        return dependencies;
    }

    /**
     * Gets the name of this container
     *
     * @return The name of this container
     */
    public String name() {
        return getClass().getSimpleName();
    }

    /**
     * Gets the object name of this container
     *
     * @return The object name of this container
     */
    public String objectName() {
        return getClass().getSimpleName().replace("Container", "");
    }

    /**
     * Creates a Tree branch for the final output
     *
     * @return a Tree branch for the final output
     */
    public Branch<String> branch() {
        String value = " > " + objectName();
        if (node == null) {
            value += " is broken";
        } else {
            value += " identified as ";
            value += node.name;
        }
        Branch<String> branch = new Branch<>(value);
        if (node != null) {
            for (Map.Entry<String, Hook> node : hooks.entrySet()) {
                if (node.getValue() == null) {
                    branch.add(new Branch<>(node.getKey() + " is broken"));
                    continue;
                }
                branch.add(node.getValue().branch());
            }
        } else {
            branch.add(new Branch<>("broken"));
        }
        return branch;
    }

}
