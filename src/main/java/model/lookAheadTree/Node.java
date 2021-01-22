package model.lookAheadTree;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {

    private T content;
    private Node<T> parent;
    private List<Node<T>> children = new ArrayList<>();
    private int height;

    //TODO unschön
    private double value = Integer.MIN_VALUE;

    public Node(T content, Node<T> parent, int height) {
        this.content = content;
        this.parent = parent;
        this.height = height;
    }

    public void addChild(Node<T> child) {
        children.add(child);
    }

    public void setValue(double value) {
        this.value = value;
    }

    public List<Node<T>> getChildren() {
        return children;
    }

    public T getContent() {
        return content;
    }

    public double getValue() {
        return value;
    }

    //TODO klassengeheimnis -> entfernen!
    public int getHeight() {
        return height;
    }
    //TODO in Board?


    //TODO combine zu getChildMinMax (boolean max, boolean min) unschön :/
    public Node<T> getChildWithHighestValue() {

        // Temporarily chosen Node representing the best Move
        Node<T> temp = new Node<>(null,null,0);

        // Value of the best Node
        double tempValue = Integer.MIN_VALUE;
        for (Node<T> child : children) {
            if (Double.compare(child.value, tempValue) > 0) {
                temp = child;
                tempValue = child.value;
            }
        }

        // Determine if the temporary node is till the original node.
        if (temp.getContent() == null) {
            throw new IllegalCallerException("This tree has no children!");
        } else {
            return temp;
        }
    }

    //TODO: combine with getBestMove
    public Node<T> getChildWithLowestValue() {

        // Temporarily chosen Node representing the best Move
        Node<T> temp = new Node<>(null,null,0);

        // Value of the worst Node
        double tempValue = Integer.MAX_VALUE;
        for (Node<T> child : this.children) {
            if (Double.compare(child.value, tempValue) < 0) {
                temp = child;
                tempValue = child.value;
            }
        }

        // Determine if the temporary node is till the original node.
        if (temp.getContent() == null) {
            throw new IllegalCallerException("This tree has no children!");
        } else {
            return temp;
        }
    }
}
