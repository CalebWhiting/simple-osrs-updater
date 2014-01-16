package edu.revtek.util.tree.basic;

import edu.revtek.util.tree.TreeNode;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

/**
 * @author Caleb Bradford
 */
public class BasicTreeNode<T> implements TreeNode<BasicTreeNode> {

    private T value;
    private BasicTreeNode parent;
    private List<BasicTreeNode> children = new Vector<>();
    private Comparator<BasicTreeNode> comparator = null;

    public BasicTreeNode() {

    }

    public BasicTreeNode(T value) {
        setValue(value);
    }

    public BasicTreeNode getParent() {
        return this.parent;
    }

    @Override
    public BasicTreeNode[] getChildren() {
        return children.toArray(new BasicTreeNode[children.size()]);
    }

    public void setParent(BasicTreeNode parent) {
        this.parent = parent;
    }

    @Override
    public void setChildren(BasicTreeNode[] children) {
        this.children.clear();
        Collections.addAll(this.children, children);
        if (getComparator() != null)
            this.children.sort(getComparator());
        for (BasicTreeNode node : children)
            node.parent = this;
    }

    public Comparator<BasicTreeNode> getComparator() {
        return this.comparator;
    }

    public void setComparator(Comparator<BasicTreeNode> comparator) {
        this.comparator = comparator;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void add(BasicTreeNode node) {
        this.children.add(node);
        setChildren(getChildren());
    }

    public void remove(BasicTreeNode node) {
        this.children.remove(node);
        node.parent = null;
        setChildren(getChildren());
    }

    public int getDepth() {
        int depth = 0;
        BasicTreeNode node = this;
        while ((node = node.getParent()) != null) {
            depth++;
        }
        return depth;
    }

    public List<BasicTreeNode> getSubNodes() {
        List<BasicTreeNode> nodes = new Vector<>();
        for (BasicTreeNode node : nodes) {
            node.addToSubNodes(nodes);
        }
        return nodes;
    }

    private void addToSubNodes(List<BasicTreeNode> nodes) {
        nodes.add(this);
        for (BasicTreeNode node : getChildren()) {
            node.addToSubNodes(nodes);
        }
    }

}
