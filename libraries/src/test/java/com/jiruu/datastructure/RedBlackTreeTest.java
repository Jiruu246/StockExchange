package com.jiruu.datastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RedBlackTreeTest {
    private RedBlackTree<Integer> tree;

    @BeforeEach
    public void setUp() {
        tree = new RedBlackTree<>(true);
    }

    // Insertion Tests
    @Test
    public void testSimpleInsertion() {
        tree.insert("10", 10);
        assertEquals(10, tree.getRoot().getData());
        assertFalse(tree.getRoot().isRed);
    }

    @Test
    public void testMultipleInsertions() {
        int[] values = {10, 5, 15, 3, 7, 12, 18};
        for (int value : values) {
            tree.insert(String.valueOf(value), value);
        }

        assertNotNull(tree.findNode(10));
        assertNotNull(tree.findNode(5));
        assertNotNull(tree.findNode(15));
        assertNotNull(tree.findNode(3));
        assertNotNull(tree.findNode(7));
        assertNotNull(tree.findNode(12));
        assertNotNull(tree.findNode(18));
    }

    @Test
    public void testInsertionMaintainsRedBlackProperties() {
        int[] values = {41, 38, 31, 12, 19, 8};
        for (int value : values) {
            tree.insert(String.valueOf(value), value);
        }

        // Check black height property
        assertTrue(isBlackHeightConsistent(tree.getRoot()));

        // Check no red node has a red child
        assertFalse(hasRedRedViolation(tree.getRoot()));
    }

    @Test
    public void testDuplicateInsertion() {
        tree.insert("10", 10);
        tree.insert("10", 10);

        // Ensure only one instance exists
        assertEquals(1, countNodes(tree.getRoot()));
    }

    // Deletion Tests
    @Test
    public void testSimpleDeletion() {
        tree.insert("10", 10);
        tree.delete("10");
        assertNull(tree.getRoot());
    }

    @Test
    public void testMultipleDeletions() {
        int[] values = {10, 5, 15, 3, 7, 12, 18};
        for (int value : values) {
            tree.insert(String.valueOf(value), value);
        }

        tree.delete("10");
        assertNull(tree.findNode(10));

        tree.delete("15");
        assertNull(tree.findNode(15));
    }

    @Test
    public void testDeletionMaintainsRedBlackProperties() {
        int[] values = {41, 38, 31, 12, 19, 8, 50, 55, 60};
        for (int value : values) {
            tree.insert(String.valueOf(value), value);
        }

        tree.delete("41");

        // Check black height property
        assertTrue(isBlackHeightConsistent(tree.getRoot()));

        // Check no red node has a red child
        assertFalse(hasRedRedViolation(tree.getRoot()));
    }

    @Test
    public void testDeletionOfNonExistentElement() {
        tree.insert("10", 10);
        tree.delete("20"); // Should not throw an exception
        assertNotNull(tree.findNode(10));
    }

    // Helper methods for property validation
    private boolean isBlackHeightConsistent(TreeNode<Integer> node) {
        if (node == null) return true;

        int leftBlackHeight = getBlackHeight(node.left);
        int rightBlackHeight = getBlackHeight(node.right);

        return leftBlackHeight == rightBlackHeight;
    }

    private int getBlackHeight(TreeNode<Integer> node) {
        if (node == null) return 1;

        int leftBlackHeight = getBlackHeight(node.left);
        int rightBlackHeight = getBlackHeight(node.right);

        if (leftBlackHeight == -1 || rightBlackHeight == -1) return -1;

        int incrementHeight = !node.isRed ? 1 : 0;

        return leftBlackHeight == rightBlackHeight
                ? leftBlackHeight + incrementHeight
                : -1;
    }

    private boolean hasRedRedViolation(TreeNode<Integer> node) {
        if (node == null) return false;

        // Check if current node is red and has a red child
        if (node.isRed) {
            if ((node.left != null && node.left.isRed) ||
                    (node.right != null && node.right.isRed)) {
                return true;
            }
        }

        // Recursively check left and right subtrees
        return hasRedRedViolation(node.left) || hasRedRedViolation(node.right);
    }

    private int countNodes(TreeNode<Integer> node) {
        if (node == null) return 0;
        return 1 + countNodes(node.left) + countNodes(node.right);
    }

    // Search and Traversal Tests
    @Test
    public void testSearch() {
        int[] values = {10, 5, 15, 3, 7, 12, 18};
        for (int value : values) {
            tree.insert(String.valueOf(value), value);
        }

        assertNotNull(tree.findNode(10));
        assertNotNull(tree.findNode(5));
        assertNull(tree.findNode(100));
    }

    // Edge Cases
    @Test
    public void testEmptyTreeOperations() {
        assertNull(tree.getRoot());
        assertNull(tree.findNode(10));
        assertDoesNotThrow(() -> tree.delete("10"));
    }

    @Test
    public void testMinMaxSignificant() {
        final RedBlackTree<Integer> minTree = new RedBlackTree<>(true);
        final RedBlackTree<Integer> maxTree = new RedBlackTree<>(false);

        int[] values = {41, 38, 31, 12, 19, 8};
        for (int value : values) {
            minTree.insert(String.valueOf(value), value);
            maxTree.insert(String.valueOf(value), value);
        }

        assertEquals(8, minTree.getSignificantData());
        assertEquals(41, maxTree.getSignificantData());

        // Test update significant edge cases
        final RedBlackTree<Integer> minTree2 = new RedBlackTree<>(true);
        minTree2.insert("10", 10);
        assertEquals(10, minTree2.getSignificantData());
        minTree2.insert("5", 5);
        assertEquals(5, minTree2.getSignificantData());
        minTree2.delete("5");
        assertEquals(10, minTree2.getSignificantData());
        minTree2.insert("15", 15);
        assertEquals(10, minTree2.getSignificantData());

        final RedBlackTree<Integer> maxTree2 = new RedBlackTree<>(false);
        maxTree2.insert("10", 10);
        assertEquals(10, maxTree2.getSignificantData());
        maxTree2.insert("5", 5);
        assertEquals(10, maxTree2.getSignificantData());
        maxTree2.delete("5");
        assertEquals(10, maxTree2.getSignificantData());
        maxTree2.insert("15", 15);
        assertEquals(15, maxTree2.getSignificantData());
    }

    @Test
    public void testDeletionUpdateCorrectSignificant() {
        final RedBlackTree<Integer> minTree = new RedBlackTree<>(true);
        final RedBlackTree<Integer> maxTree = new RedBlackTree<>(false);

        minTree.insert("10", 10);
        minTree.insert("20", 20);
        minTree.delete("10");
        assertEquals(20, minTree.getSignificantData());
        minTree.delete("20");
        assertNull(minTree.getSignificantData());
        assertFalse(minTree.delete("20"));

        minTree.insert("20", 20);
        minTree.insert("10", 10);
        minTree.delete("10");
        assertEquals(20, minTree.getSignificantData());
        minTree.delete("20");
        assertNull(minTree.getSignificantData());
        assertFalse(minTree.delete("20"));

        maxTree.insert("10", 10);
        maxTree.insert("20", 20);
        maxTree.delete("10");
        assertEquals(20, maxTree.getSignificantData());
        maxTree.delete("20");
        assertNull(maxTree.getSignificantData());
        assertFalse(maxTree.delete("20"));

        maxTree.insert("20", 20);
        maxTree.insert("10", 10);
        maxTree.delete("10");
        assertEquals(20, maxTree.getSignificantData());
        maxTree.delete("20");
        assertNull(maxTree.getSignificantData());
        assertFalse(maxTree.delete("20"));
    }
}