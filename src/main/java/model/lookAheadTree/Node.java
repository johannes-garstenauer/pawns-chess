package model.lookAheadTree;

import java.util.ArrayList;
import java.util.List;

/**
 * A node class which is used to construct a tree for use in a min-max
 * algorithm.
 *
 * @param <T> A generic class for the content of the node.
 */
public class Node<T> {

    /**
     * The content of the node.
     */
    private T content;

    /**
     * The parent node.
     */
    private Node<T> parent;

    /**
     * The children nodes.
     */
    private List<Node<T>> children = new ArrayList<>();

    /**
     * The nodes height in the tree.
     */
    private int height;

    /**
     * The value which the node holds. It is determined by the nodes content
     * and initialized as the {@code Integer.MIN_VALUE} to ease any
     * comparisons.
     */
    private double value = Integer.MIN_VALUE;

    /**
     * A constructor for a node object.
     *
     * @param content The content of the node.
     * @param parent The nodes parent in a tree. {@code null} if the node is
     *               a root.
     * @param height The height of the node in a tree.
     */
    public Node(T content, Node<T> parent, int height) {
        this.content = content;
        this.parent = parent;
        this.height = height;
    }

    /**
     * Add a child to this node children.
     *
     * @param child The node which is added to this nodes children.
     */
    public void addChild(Node<T> child) {
        children.add(child);
    }

    /**
     * Setter for the value of the node.
     *
     * @param value The value to which the nodes value is set.
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * The getter for the nodes children.
     *
     * @return Returns the nodes list of children.
     */
    public List<Node<T>> getChildren() {
        return children;
    }

    /**
     * The getter for the nodes content.
     *
     * @return Return the nodes content.
     */
    public T getContent() {
        return content;
    }

    /**
     * The getter for the nodes value.
     *
     * @return The value of the node.
     */
    public double getValue() {
        return value;
    }

    /**
     * The getter for the nodes height.
     *
     * @return The height of the node.
     */
    public int getHeight() {
        return height;
    }

    //TODO combine zu getChildMinMax (boolean max, boolean min) unschön :/
    /**
     * The getter for the child of this node with the highest value.
     *
     * @return Returns the child with the highest node.
     */
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
    /**
     * The getter for the child of this node with the lowest value.
     *
     * @return Returns the child with the lowest node.
     */
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
