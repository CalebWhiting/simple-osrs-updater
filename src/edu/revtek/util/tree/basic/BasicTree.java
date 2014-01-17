package edu.revtek.util.tree.basic;

import edu.revtek.util.tree.Tree;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Caleb Bradford
 */
public class BasicTree implements Tree<BasicTreeNode> {

    private BasicTreeNode root;

    public BasicTree() {

    }

    public BasicTree(BasicTreeNode root) {
        this.root = root;
    }

    @Override
    public BasicTreeNode getRoot() {
        return this.root;
    }

    @Override
    public void setRoot(BasicTreeNode root) {
        this.root = root;
    }

    public void write(OutputStream out) {
        write(out, getRoot(), 0);
    }

    private void write(OutputStream out, BasicTreeNode node, int i) {
        StringBuilder build = new StringBuilder();
        for (int i1 = 0; i1 < i; i1++) {
            build.append('\t');
        }
        build.append(node.valueToString());
        build.append('\n');
        try {
            out.write(build.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        i++;
        for (BasicTreeNode sub : node.getChildren()) {
            write(out, sub, i);
        }
    }

}
