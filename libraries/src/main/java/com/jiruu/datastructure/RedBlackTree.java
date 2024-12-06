package com.jiruu.datastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RedBlackTree<T extends Comparable<T>> {

    private TreeNode<T> root;
    private Map<String, TreeNode<T>> map;
    private TreeNode<T> significantNode;
    private final boolean favorMin;

    public RedBlackTree(boolean favorMin) {
        root = null;
        map = new java.util.HashMap<>();
        this.favorMin = favorMin;
        significantNode = null;
    }

    public TreeNode<T> getRoot() {
        return root;
    }

    public T getSignificantData() {
        return significantNode == null ? null : significantNode.getData();
    }

    public boolean isEmpty() {
        return root == null;
    }

    // Insert a new value into the tree, O(log(n)) time
    public boolean insert(String key, T data) {
        if (map.containsKey(key)) {
            return false;
        }

        TreeNode<T> node = new TreeNode<>(data);
        map.put(key, node);

        // If tree is empty, make the node the root and color it black
        // set the significant node to the root
        if (root == null) {
            root = node;
            root.isRed = false;
            significantNode = root;
            return true;
        }

        // First, perform standard BST insertion
        TreeNode<T> parent = null;
        TreeNode<T> current = root;
        while (current != null) {
            parent = current;
            if (data.compareTo(current.getData()) < 0) {
                current = current.left;
            } else if (data.compareTo(current.getData()) > 0) {
                current = current.right;
            } else {
                // Duplicate values are not allowed
                return false;
            }
        }

        // Attach the new node
        node.parent = parent;
        if (data.compareTo(parent.getData()) < 0) {
            parent.left = node;
        } else {
            parent.right = node;
        }

        // Fix Red-Black Tree properties
        fixInsertion(node);

        // Case where the new inserted node pushed the significant node up, so we need to update it
        if (significantNode.left != null && favorMin) {
            significantNode = significantNode.left;
        } else if (significantNode.right != null && !favorMin) {
            significantNode = significantNode.right;
        }

        return true;
    }

    // Fix Red-Black Tree properties after insertion
    private void fixInsertion(TreeNode<T> node) {
        TreeNode<T> parent;
        TreeNode<T> grandparent;

        while (node != root && node.isRed && node.parent.isRed) {
            parent = node.parent;
            grandparent = parent.parent;

            // Case A: Parent is left child of grandparent
            if (parent == grandparent.left) {
                TreeNode<T> uncle = grandparent.right;

                // Case 1: Uncle is also red - Color flip
                if (uncle != null && uncle.isRed) {
                    grandparent.isRed = true;
                    parent.isRed = false;
                    uncle.isRed = false;
                    node = grandparent;
                } else {
                    // Case 2: TreeNode is right child - Left Rotation
                    if (node == parent.right) {
                        rotateLeft(parent);
                        node = parent;
                        parent = node.parent;
                    }

                    // Case 3: TreeNode is left child - Right Rotation
                    rotateRight(grandparent);
                    boolean tempColor = parent.isRed;
                    parent.isRed = grandparent.isRed;
                    grandparent.isRed = tempColor;
                    node = parent;
                }
            }
            // Case B: Parent is right child of grandparent
            else {
                TreeNode<T> uncle = grandparent.left;

                // Case 1: Uncle is also red - Color flip
                if (uncle != null && uncle.isRed) {
                    grandparent.isRed = true;
                    parent.isRed = false;
                    uncle.isRed = false;
                    node = grandparent;
                } else {
                    // Case 2: TreeNode is left child - Right Rotation
                    if (node == parent.left) {
                        rotateRight(parent);
                        node = parent;
                        parent = node.parent;
                    }

                    // Case 3: TreeNode is right child - Left Rotation
                    rotateLeft(grandparent);
                    boolean tempColor = parent.isRed;
                    parent.isRed = grandparent.isRed;
                    grandparent.isRed = tempColor;
                    node = parent;
                }
            }
        }

        // Ensure root is always black
        root.isRed = false;
    }

    // Left rotation
    private void rotateLeft(TreeNode<T> node) {
        TreeNode<T> rightChild = node.right;
        node.right = rightChild.left;

        if (node.right != null) {
            node.right.parent = node;
        }

        rightChild.parent = node.parent;

        if (node.parent == null) {
            root = rightChild;
        } else if (node == node.parent.left) {
            node.parent.left = rightChild;
        } else {
            node.parent.right = rightChild;
        }

        rightChild.left = node;
        node.parent = rightChild;
    }

    // Right rotation
    private void rotateRight(TreeNode<T> node) {
        TreeNode<T> leftChild = node.left;
        node.left = leftChild.right;

        if (node.left != null) {
            node.left.parent = node;
        }

        leftChild.parent = node.parent;

        if (node.parent == null) {
            root = leftChild;
        } else if (node == node.parent.right) {
            node.parent.right = leftChild;
        } else {
            node.parent.left = leftChild;
        }

        leftChild.right = node;
        node.parent = leftChild;
    }

    // Perform O(1) deletion
    public boolean delete(String key) {
        TreeNode<T> node = map.get(key);
        if (node == null) return false;

        if (node == significantNode) {
            if (significantNode == root) {
                significantNode = favorMin ? significantNode.right : significantNode.left;
            } else {
                significantNode = node.parent;
            }
        }

        deleteNode(node);
        map.remove(key);
        return true;
    }

    // Find a node with given data
    public TreeNode<T> findNode(T data) {
        TreeNode<T> current = root;
        while (current != null) {
            if (data.compareTo(current.getData()) == 0) {
                return current;
            } else if (data.compareTo(current.getData()) < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return null;
    }

    // Delete a node from the tree
    // FIXME: vulnerable to stack overflow
    private void deleteNode(TreeNode<T> node) {
        TreeNode<T> replacement = findReplacement(node);
        TreeNode<T> parent = node.parent;

        // No children case
        if (replacement == null) {
            if (node == root) {
                root = null;
            } else {
                if (isBlack(node)) {
                    // Handle double black
                    fixDoubleBlack(node);
                }

                if (parent != null) {
                    if (node == parent.left) {
                        parent.left = null;
                    } else {
                        parent.right = null;
                    }
                }
            }
            return;
        }

        // One child case
        if (node.left == null || node.right == null) {
            if (node == root) {
                replacement.parent = null;
                root = replacement;
            } else {
                if (node == parent.left) {
                    parent.left = replacement;
                } else {
                    parent.right = replacement;
                }
                replacement.parent = parent;

                if (isBlack(node)) {
                    // Black node replaced
                    if (isRed(replacement)) {
                        replacement.isRed = false;
                    } else {
                        fixDoubleBlack(replacement);
                    }
                }
            }
            return;
        }

        // Two children case
        swapNodes(node, replacement);
        deleteNode(node);
    }

    // Find replacement node for deletion
    private TreeNode<T> findReplacement(TreeNode<T> node) {
        // No children
        if (node.left == null && node.right == null) {
            return null;
        }

        // Only left child
        if (node.left != null && node.right == null) {
            return node.left;
        }

        // Only right child
        if (node.left == null) {
            return node.right;
        }

        // Two children - find inorder successor
        TreeNode<T> successor = node.right;
        while (successor.left != null) {
            successor = successor.left;
        }
        return successor;
    }

    // Fix double black node issue during deletion
    // FIXME: vulnerable to stack overflow
    private void fixDoubleBlack(TreeNode<T> node) {
        if (node == root) return;

        TreeNode<T> sibling = getSibling(node);
        TreeNode<T> parent = node.parent;

        if (sibling == null) {
            // No sibling, propagate double black to parent
            fixDoubleBlack(parent);
        } else {
            if (isRed(sibling)) {
                // Red sibling case
                parent.isRed = true;
                sibling.isRed = false;
                if (sibling == parent.left) {
                    rotateRight(parent);
                } else {
                    rotateLeft(parent);
                }
                fixDoubleBlack(node);
            } else {
                // Black sibling cases
                if (hasRedChild(sibling)) {
                    // At least one red child
                    if (sibling == parent.left) {
                        if (isRed(sibling.left)) {
                            // Left-Left case
                            sibling.left.isRed = sibling.isRed;
                            sibling.isRed = parent.isRed;
                            rotateRight(parent);
                        } else {
                            // Left-Right case
                            sibling.right.isRed = parent.isRed;
                            rotateLeft(sibling);
                            rotateRight(parent);
                        }
                    } else {
                        if (isRed(sibling.right)) {
                            // Right-Right case
                            sibling.right.isRed = sibling.isRed;
                            sibling.isRed = parent.isRed;
                            rotateLeft(parent);
                        } else {
                            // Right-Left case
                            sibling.left.isRed = parent.isRed;
                            rotateRight(sibling);
                            rotateLeft(parent);
                        }
                    }
                    parent.isRed = false;
                } else {
                    // Both children are black
                    sibling.isRed = true;
                    if (!parent.isRed) {
                        fixDoubleBlack(parent);
                    } else {
                        parent.isRed = false;
                    }
                }
            }
        }
    }

    // Get sibling of a node
    private TreeNode<T> getSibling(TreeNode<T> node) {
        if (node.parent == null) return null;
        return (node == node.parent.left) ? node.parent.right : node.parent.left;
    }

    // Check if node is black
    private boolean isBlack(TreeNode<T> node) {
        return node == null || !node.isRed;
    }

    // Check if node is red
    private boolean isRed(TreeNode<T> node) {
        return node != null && node.isRed;
    }

    // Check if sibling has a red child
    private boolean hasRedChild(TreeNode<T> node) {
        return (node.left != null && node.left.isRed) ||
                (node.right != null && node.right.isRed);
    }

    // Inorder traversal
    public List<T> inorderTraversal() {
        List<T> result = new ArrayList<>();
        inOrderHelper(root, result);
        return result;
    }

    private void inOrderHelper(TreeNode<T> node, List<T> result) {
        if (node == null) return;
        inOrderHelper(node.left, result);
        result.add(node.getData());
        inOrderHelper(node.right, result);
    }

    private void swapNodes(TreeNode<T> node1, TreeNode<T> node2) {
        final TreeNode<T> parent, left, right;
        final boolean isRed;
        parent = node1.parent;
        left = node1.left;
        right = node1.right;
        isRed = node1.isRed;

        node1.parent = node2.parent;
        node1.left = node2.left;
        node1.right = node2.right;
        node1.isRed = node2.isRed;

        node2.parent = parent;
        node2.left = left;
        node2.right = right;
        node2.isRed = isRed;
    }
}