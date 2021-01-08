package model;

import java.util.Stack;

public class LookAheadTree //implements Cloneable
{
    private final Node root;

    public LookAheadTree (BoardImpl board) {
        this.root = new Node(board, null, 0);
    }

    public void constructTree(int level){
        root.createSubTree(level);
    }

    public void setValues() {

    }
}
