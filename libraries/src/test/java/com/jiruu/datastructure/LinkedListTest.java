package com.jiruu.datastructure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTest {

    @Test
    void getHead() {
        final LinkedList<Integer> linkedList = new LinkedList<>();
        linkedList.add(1, "1");
        linkedList.add(2, "2");
        linkedList.add(3, "3");

        assertEquals(1, linkedList.getHead().data);
    }

    @Test
    void getTail() {
        final LinkedList<Integer> linkedList = new LinkedList<>();
        linkedList.add(1, "1");
        linkedList.add(2, "2");
        linkedList.add(3, "3");

        assertEquals(3, linkedList.getTail().data);
    }

    @Test
    void add() {
        final LinkedList<Integer> linkedList = new LinkedList<>();
        linkedList.add(1, "1");
        linkedList.add(2, "2");
        linkedList.add(3, "3");

        assertEquals(1, linkedList.getHead().data);
        assertEquals(3, linkedList.getTail().data);

        assertFalse(linkedList.add(3, "3"));
    }

    @Test
    void remove() {
        final LinkedList<Integer> linkedList = new LinkedList<>();
        linkedList.add(1, "1");
        linkedList.add(2, "2");
        linkedList.add(3, "3");

        assertEquals(1, linkedList.remove("1"));
        assertEquals(2, linkedList.getHead().data);

        assertEquals(3, linkedList.remove("3"));
        assertEquals(2, linkedList.getTail().data);

        assertEquals(2, linkedList.remove("2"));
        assertNull(linkedList.getHead());
        assertNull(linkedList.getTail());

        assertNull(linkedList.remove("4"));

        linkedList.add(1, "1");
        linkedList.add(2, "2");
        linkedList.add(3, "3");

        assertEquals(2, linkedList.remove("2"));
        assertEquals(3, linkedList.getTail().data);
    }

    @Test
    void get() {
        final LinkedList<Integer> linkedList = new LinkedList<>();
        linkedList.add(1, "1");
        linkedList.add(2, "2");
        linkedList.add(3, "3");

        assertEquals(1, linkedList.get("1"));
        assertEquals(2, linkedList.get("2"));
        assertEquals(3, linkedList.get("3"));
        assertNull(linkedList.get("4"));
    }
}