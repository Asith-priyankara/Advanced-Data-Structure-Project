public class LeftistTree {

    public class Node {
        int vertex;
        int key;
        Node leftChild;
        Node rightChild;
        int nullPathLength;

        Node(int vertex, int key) {
            this.vertex = vertex;
            this.key = key;
            this.leftChild = null;
            this.rightChild = null;
            this.nullPathLength = 0;
        }
    }

    private Node root;

    public LeftistTree() {
        root = null;
    }

    public void insert(int vertex, int key) {
        Node newNode = new Node(vertex, key);
        root = meld(root, newNode);
    }

    public Node deleteMin() {
        if (root == null) return null;
        Node minNode = root;
        root = meld(root.leftChild, root.rightChild);
        return minNode;
    }

    public boolean isEmpty() {
        return root == null;
    }

    private Node meld(Node tree1, Node tree2) {
        if (tree1 == null) return tree2;
        if (tree2 == null) return tree1;

        if (tree1.key > tree2.key) {
            Node temp = tree1;
            tree1 = tree2;
            tree2 = temp;
        }

        tree1.rightChild = meld(tree1.rightChild, tree2);

        if (tree1.leftChild == null) {
            tree1.leftChild = tree1.rightChild;
            tree1.rightChild = null;
        } else {
            if (tree1.rightChild != null && tree1.leftChild.nullPathLength < tree1.rightChild.nullPathLength) {
                Node temp = tree1.leftChild;
                tree1.leftChild = tree1.rightChild;
                tree1.rightChild = temp;
            }
            tree1.nullPathLength = (tree1.rightChild != null ? tree1.rightChild.nullPathLength : 0) + 1;
        }

        return tree1;
    }

    public void decreaseKey(int vertex, int newKey) {
        delete(vertex);
        insert(vertex, newKey);
    }

    private void delete(int vertex) {
        root = delete(root, vertex);
    }

    private Node delete(Node currentRoot, int vertex) {
        if (currentRoot == null) return null;
        if (currentRoot.vertex == vertex) {
            return meld(currentRoot.leftChild, currentRoot.rightChild);
        } else {
            currentRoot.leftChild = delete(currentRoot.leftChild, vertex);
            currentRoot.rightChild = delete(currentRoot.rightChild, vertex);
            return currentRoot;
        }
    }
}