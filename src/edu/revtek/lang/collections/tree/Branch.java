package edu.revtek.lang.collections.tree;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * @author Caleb Whiting
 */
public class Branch<T> {

    private T value;
    private Branch parent;
    private List<Branch> children = new Vector<>();

    public Branch() {

    }

    public Branch(T value) {
        setValue(value);
    }

    public Branch getParent() {
        return this.parent;
    }

    public Branch[] getChildren() {
        return children.toArray(new Branch[children.size()]);
    }

    public void setChildren(Branch[] children) {
        this.children.clear();
        Collections.addAll(this.children, children);
        for (Branch node : children)
            node.parent = this;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void add(Branch node) {
        this.children.add(node);
        setChildren(getChildren());
    }

    public void remove(Branch node) {
        this.children.remove(node);
        node.parent = null;
        setChildren(getChildren());
    }

    public String valueToString() {
        return getValue().toString();
    }
}
