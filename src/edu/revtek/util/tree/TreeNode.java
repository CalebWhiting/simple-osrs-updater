package edu.revtek.util.tree;

/**
 * @author Caleb Whiting
 */
public interface TreeNode<T extends TreeNode> {

    public T[] getChildren();

    public void setChildren(T[] children);

}
