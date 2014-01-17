package edu.revtek.lang.collections.tree;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Caleb Whiting
 */
public class Tree {

    private String tab = "\t";
    private Branch root;

    public Tree() {

    }

    public Tree(Branch root) {
        this.root = root;
    }

    public Branch getRoot() {
        return this.root;
    }

    public void setRoot(Branch root) {
        this.root = root;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public void write(OutputStream out) {
        write(out, getRoot(), 0);
    }

    private void write(OutputStream out, Branch node, int i) {
        StringBuilder build = new StringBuilder();
        for (int i1 = 0; i1 < i; i1++) {
            build.append(tab);
        }
        build.append(node.valueToString());
        build.append('\n');
        try {
            out.write(build.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        i++;
        for (Branch sub : node.getChildren()) {
            write(out, sub, i);
        }
    }

}
