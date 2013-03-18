package tests;

import java.util.Random;

import main.BinaryTree;
import main.BinaryTree.Node;

/**
 * Test class for the BinaryTree class.
 * 
 * @author Fabian Foerg
 * 
 */
public final class Tests {

    /**
     * The number of nodes in the test tree to create.
     */
    private static final int NUMBER_NODES = 30;
    /**
     * The range of node keys.
     */
    private static final int NODE_KEY_RANGE = 100;

    /**
     * Hidden constructor.
     */
    private Tests() {
    }

    /**
     * Runs the tests.
     * 
     * @param args
     *            ignored.
     */
    public static void main(String[] args) {
        BinaryTree<Integer> tree = new BinaryTree<Integer>();
        Random random = new Random();

        // build tree with random node keys
        for (int i = 1; i <= NUMBER_NODES; i++) {
            int randomKey = random.nextInt(NODE_KEY_RANGE);
            Node<Integer> node = new Node<Integer>(randomKey);
            tree.insert(node);
        }

        // encode and decode the tree
        String encoded = tree.encode();
        String decoded = BinaryTree.decode(encoded).encode();

        if (encoded.equals(decoded)) {
            System.out.println("Test runs completed successfully!");
        } else {
            System.err.println("Error encoding or decoding the tree!");
        }
    }
}
