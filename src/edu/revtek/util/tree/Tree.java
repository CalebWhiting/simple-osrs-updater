package edu.revtek.util.tree;

/**
 * @author Caleb Bradford
 */
public interface Tree<T extends TreeNode> {

    public T getRoot();

    public void setRoot(T root);

}
