package main;

import java.util.Stack;

/**
 * This class represents a binary tree.
 * 
 * @author Fabian Foerg
 * 
 * @param <T>
 *            the node key type.
 */
public final class BinaryTree<T extends Comparable<T>> {
    private Node<T> root;

    private static final char OPEN_BRACE = '(';
    private static final char CLOSE_BRACE = ')';

    private static final char LEFT_NODE_SYMBOL = 'l';
    private static final char RIGHT_NODE_SYMBOL = 'r';

    /**
     * Creates an empty binary tree.
     */
    public BinaryTree() {
        this.root = null;
    }

    /**
     * Creates a binary tree having the given root.
     * 
     * @param root
     *            the root node of the new tree.
     */
    public BinaryTree(Node<T> root) {
        this.root = root;
    }

    /**
     * Inserts the given node into this tree. If there is already a node that
     * has the same key in the tree, the given node will not be inserted.
     * 
     * @param node
     *            the node to insert.
     */
    public void insert(Node<T> node) {
        if ((node == null) || node.hasLeft() || node.hasRight()) {
            throw new IllegalArgumentException(
                    "node must not be null and must not have children!");
        }

        if (root == null) {
            root = node;
        } else {
            Node<T> current = root;

            while (current != null) {
                int comparison = node.getKey().compareTo(current.getKey());

                if (comparison < 0) {
                    if (current.hasLeft()) {
                        current = current.getLeft();
                    } else {
                        current.setLeft(node);
                        break;
                    }
                } else if (comparison > 0) {
                    if (current.hasRight()) {
                        current = current.getRight();
                    } else {
                        current.setRight(node);
                        break;
                    }
                } else {
                    // do not insert nodes with the same key.
                    break;
                }
            }
        }
    }

    /**
     * Encodes the tree as a string and returns the result.
     * 
     * @return the string representation of this tree.
     */
    public String encode() {
        StringBuilder builder = new StringBuilder();

        if (root != null) {
            encode(root, builder);
        }

        return builder.toString();
    }

    /**
     * Encodes the given node and its sub-nodes as a string.
     * 
     * @param node
     *            the node to encode.
     * @param builder
     *            the string builder that encodes the string.
     */
    private void encode(Node<T> node, StringBuilder builder) {
        assert (node != null) && (builder != null);

        builder.append(node.getKey().toString());

        if (node.hasLeft()) {
            builder.append(OPEN_BRACE);
            builder.append(LEFT_NODE_SYMBOL);
            encode(node.getLeft(), builder);
            builder.append(CLOSE_BRACE);
        }

        if (node.hasRight()) {
            builder.append(OPEN_BRACE);
            builder.append(RIGHT_NODE_SYMBOL);
            encode(node.getRight(), builder);
            builder.append(CLOSE_BRACE);
        }
    }

    /**
     * Creates a binary tree from the given string representation. The node keys
     * must be integers.
     * 
     * @param encodedTree
     *            the string representation the tree to create.
     * @return the created tree or <code>null</code> if the string is in the
     *         wrong format or is empty.
     */
    public static BinaryTree<Integer> decode(String encodedTree) {
        BinaryTree<Integer> result = null;
        int nextNode = encodedTree.indexOf(OPEN_BRACE);
        int keyEnd = (nextNode > 0) ? nextNode : encodedTree.length();

        if (keyEnd > 0) {
            try {
                Integer root = Integer.parseInt(encodedTree
                        .substring(0, keyEnd));
                Node<Integer> rootNode = new Node<Integer>(root);
                if (nextNode > 0) {
                    String encodedSubTree = encodedTree.substring(keyEnd + 1);
                    // decode sub-tree of root node
                    decode(rootNode, encodedSubTree);
                    // decode possibly existing right sub-tree of root node
                    decodeRight(rootNode, encodedSubTree);
                }
                result = new BinaryTree<Integer>(rootNode);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                throw new IllegalArgumentException(
                        "The given string is not in the right format!");
            }
        }

        return result;
    }

    /**
     * Decodes the given node, thereby building a tree with integer node keys.
     * 
     * @param root
     *            the root node of the given sub-tree string.
     * @param encodedSubTree
     *            the string builder with the encoded string. Must have integer
     *            node keys.
     */
    private static void decode(Node<Integer> root, String encodedSubTree) {
        assert (root != null) && (encodedSubTree != null);

        int index = 0;
        boolean done = false;
        char symbol;
        int nextOpen;
        int nextClose;
        String toParse;

        // skip braces
        while ((index < encodedSubTree.length()) && !done) {
            switch (encodedSubTree.charAt(index)) {
            case OPEN_BRACE:
            case CLOSE_BRACE:
                index++;
                break;

            default:
                done = true;
                break;
            }
        }

        // check whether we are done
        if ((index + 1) >= encodedSubTree.length()) {
            return;
        }

        // parse the node
        symbol = encodedSubTree.charAt(index);

        // unless the node is the root node, there must be a node
        // type symbol
        if ((index != 0) && (symbol != LEFT_NODE_SYMBOL)
                && (symbol != RIGHT_NODE_SYMBOL)) {
            throw new IllegalArgumentException(
                    "The given string is not in the right format!");
        }

        toParse = encodedSubTree.substring(index + 1);
        nextOpen = toParse.indexOf(OPEN_BRACE);
        nextClose = toParse.indexOf(CLOSE_BRACE);

        if (Math.max(nextOpen, nextClose) > 0) {
            // there is at least one node left
            int keyEnd = ((nextOpen > 0) && (nextOpen < nextClose)) ? nextOpen
                    : nextClose;

            try {
                Integer current = Integer
                        .parseInt(toParse.substring(0, keyEnd));
                Node<Integer> currentNode = new Node<Integer>(current);

                if (symbol == LEFT_NODE_SYMBOL) {
                    root.setLeft(currentNode);
                } else {
                    root.setRight(currentNode);
                }

                if (keyEnd == nextOpen) {
                    // this node has at least one child
                    String child = toParse.substring(keyEnd + 1);
                    decode(currentNode, child);
                    // now decode possibly existing right child
                    decodeRight(currentNode, child);
                }
                // else done with sub-tree
            } catch (NumberFormatException e) {
                e.printStackTrace();
                throw new IllegalArgumentException(
                        "The given string is not in the right format!");
            }
        }
    }

    /**
     * Decodes a right child node.
     * 
     * @param root
     *            the root node of the sub-tree.
     * @param encodedSubTree
     *            a string representation of the sub-tree.
     */
    private static void decodeRight(Node<Integer> root, String encodedSubTree) {
        assert (root != null) && (encodedSubTree != null);

        Stack<Character> bracketStack = new Stack<Character>();
        bracketStack.push(OPEN_BRACE);
        int index = 0;

        while (!bracketStack.isEmpty() && (index < encodedSubTree.length())) {
            switch (encodedSubTree.charAt(index)) {
            case OPEN_BRACE:
                bracketStack.push(CLOSE_BRACE);
                break;

            case CLOSE_BRACE:
                bracketStack.pop();
                break;

            default:
                break;
            }

            index++;
        }

        if ((index < encodedSubTree.length())
                && (encodedSubTree.charAt(index) != CLOSE_BRACE)) {
            // skip possibly existing opening brace
            index++;

            // decode right sub-tree
            decode(root, encodedSubTree.substring(index));
        }
    }

    /**
     * Represents a node of a tree.
     * 
     * @author Fabian Foerg
     * 
     * @param <T>
     *            the node key type
     */
    public static class Node<T extends Comparable<T>> {
        private T key;
        private Node<T> left;
        private Node<T> right;

        /**
         * Creates a new Node with the given key.
         * 
         * @param key
         *            the key of the node to create.
         */
        public Node(T key) {
            if (key == null) {
                throw new IllegalArgumentException("key must not be null!");
            }

            this.key = key;
            left = null;
            right = null;
        }

        /**
         * Returns the key of this node.
         * 
         * @return the key of this node.
         */
        public T getKey() {
            return key;
        }

        /**
         * Returns the left child node.
         * 
         * @return the left child node.
         */
        public Node<T> getLeft() {
            return left;
        }

        /**
         * Returns the right child node.
         * 
         * @return the right child node.
         */
        public Node<T> getRight() {
            return right;
        }

        /**
         * Sets the left child node.
         * 
         * @param node
         *            the left child node.
         */
        public void setLeft(Node<T> node) {
            left = node;
        }

        /**
         * Sets the right child node.
         * 
         * @param node
         *            the right child node.
         */
        public void setRight(Node<T> node) {
            right = node;
        }

        /**
         * Returns whether this node has a left child node.
         * 
         * @return <code>true</code>, if this node has a left child node.
         *         Otherwise, <code>false</code>.
         */
        public boolean hasLeft() {
            return left != null;
        }

        /**
         * Returns whether this node has a right child node.
         * 
         * @return <code>true</code>, if this node has a right child node.
         *         Otherwise, <code>false</code>.
         */
        public boolean hasRight() {
            return right != null;
        }

        /**
         * Returns whether this node has a child node.
         * 
         * @return <code>true</code>, if this node has at least one child node.
         *         Otherwise, <code>false</code>.
         */
        public boolean hasChild() {
            return hasLeft() || hasRight();
        }
    }
}
