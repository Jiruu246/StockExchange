package com.jiruu.datastructure;

import java.util.HashMap;
import java.util.Map;

public class LinkedList<T> {
    private Node<T> head;
    private Node<T> tail;
    private final Map<String, Node<T>> map;

    public LinkedList() {
        head = null;
        tail = null;
        map = new HashMap<>();
    }

    //Node is exposed? doesn't seem like a good idea
    protected Node<T> getHead() {
        return head;
    }

    protected Node<T> getTail() {
        return tail;
    }

    public T getHeadData() {
        return head == null ? null : head.data;
    }

    public boolean add(T data, String key) {
        if (map.containsKey(key)) {
            return false;
        }

        Node<T> newNode = new Node<>();
        newNode.data = data;
        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }
        tail = newNode;

        map.put(key, newNode);
        return true;
    }

    public T remove(String key) {
        Node<T> node = map.get(key);

        if (node == null) {
            return null;
        }

        // If node is head
        if (node == head)
        {
            head = node.next;
            if (head != null)
                head.prev = null;
            else
                tail = null;
        }
        // If node is tail
        else if (node == tail)
        {
            tail = node.prev;
            tail.next = null;
        }
        // If node is in middle
        else
        {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        map.remove(key);
        return node.data;
    }

    public T get(String key) {
        Node<T> node = map.get(key);
        return node == null ? null : node.data;
    }
}
