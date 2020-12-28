package model;

public class LookAheadTree implements Cloneable{
    private Node root;

    public LookAheadTree (BoardImpl board) {
        this.root = new Node(board, null);
    }

    public Node getRoot() {
        return root;
    }

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
}
