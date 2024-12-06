package com.jiruu.datastructure;

public class Node<T> {
    public T data;
    public Node<T> next;
    public Node<T> prev;

    public Node() {
        this.data = null;
        this.next = null;
        this.prev = null;
    }
}
