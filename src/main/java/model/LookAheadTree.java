package model;

import java.util.Stack;

public class LookAheadTree //implements Cloneable
{
    private final Node root;

    public LookAheadTree (BoardImpl board) {
        this.root = new Node(board, null);
    }

    public void constructTree(int level){
        root.createSubTree(level);
    }


    /*
    public Node getBestMove(){

        // Temporarily chosen Node representing the best Move
        Node temp = null;

        // Value of the chosen Node
        double tempValue = Double.MIN_VALUE;
        for (Node child: root.children) {
            if (child.getValue() > tempValue) {
                temp = child;
                tempValue = child.getValue();
            }
        }

        if (temp == null) {
            //TODO: right exc??
            throw new IllegalCallerException("This tree has no children!");
        } else  {
            return temp;
        }
    }
     */
    /*
    public void createNodeValues() {

        // A stack for depth first search in tree.
        Stack<Node> DFS_stack = new Stack<>();

        for (Node child : root.get) {

            if(child.value == Double.MIN_VALUE) {
                child.createNodeValue();
            } else {

            }
        }
    }

     */
    /*
    @Override
    public LookAheadTree clone(){
        LookAheadTree copy;

        try {
            copy = (LookAheadTree) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }

        copy.root = root.clone();
        return copy;
    }
     */
}
