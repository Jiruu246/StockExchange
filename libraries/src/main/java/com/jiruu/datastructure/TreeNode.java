package com.jiruu.datastructure;

public class TreeNode<T> {
    // Immutable data because the data is used as a key in the RedBlackTree
    private T data;
    public TreeNode<T> left, right, parent;
    public boolean isRed;

    public TreeNode(T data) {
        this.data = data;
        this.isRed = true; // New nodes are always red initially
    }

    public T getData() {
        return data;
    }
}
