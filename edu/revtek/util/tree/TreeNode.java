package edu.revtek.util.tree;

/**
 * @author Caleb Bradford
 */
public interface TreeNode<T extends TreeNode> {

    public T[] getChildren();

    public void setChildren(T[] children);

}
