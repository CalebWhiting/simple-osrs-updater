package edu.revtek.util.tree;

/**
 * @author Caleb Whiting
 */
public interface Tree<T extends TreeNode> {

    public T getRoot();

    public void setRoot(T root);

}
