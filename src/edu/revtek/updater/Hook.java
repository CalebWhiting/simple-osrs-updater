package edu.revtek.updater;

import edu.revtek.util.tree.basic.BasicTreeNode;

/**
 * @author Caleb Bradford
 *
 * A wrapper for holding data relevent to a hook value
 */
public class Hook {

    public final String owner;
    public final String name;
    public final String desc;

    public Hook(String owner, String name, String desc) {
        this.owner = owner;
        this.name = name;
        this.desc = desc;
    }

    public BasicTreeNode<String> branch(String key) {
        return new BasicTreeNode<>(key + " identified as '" + desc + " " + owner + "." + name + "'");
    }

}
