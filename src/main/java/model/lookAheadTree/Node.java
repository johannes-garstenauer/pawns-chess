package model.lookAheadTree;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {

    private T content;
    private Node<T> parent;
    private List<Node<T>> children = new ArrayList<>();
    private int height;

    //TODO unschön
    private double value; //= Integer.MIN_VALUE;

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
    public Node<T> getBestChild() {

        // Temporarily chosen Node representing the best Move
        Node<T> temp = null;

        // Value of the chosen Node
        double tempValue = Integer.MIN_VALUE;
        for (Node<T> child : children) {
            //TODO put this calculation in if clause
            int doubleComp = Double.compare(child.value, tempValue);
            if (doubleComp > 0) {
                temp = child;
                tempValue = child.value;
            }
        }

        if (temp == null) {
            //TODO: right exc??
            throw new IllegalCallerException("This tree has no children!");
        } else {
            return temp;
        }
    }

    //TODO: combine with getBestMove
    public Node<T> getWorstChild() {

        // Temporarily chosen Node representing the best Move
        Node<T> temp = null;

        // Value of the chosen Node
        double tempValue = Integer.MAX_VALUE;
        for (Node<T> child : this.children) {
            int doubleComp = Double.compare(child.value, tempValue);
            if (doubleComp < 0) {
                temp = child;
                tempValue = child.value;
            }
        }

        if (temp == null) {
            //TODO: right exc??
            //ablaufsteuerung mit exc?
            throw new IllegalCallerException("This tree has no children!");
        } else {
            return temp;
        }
    }
}
