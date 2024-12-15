public class LeftistTree {

    private class Node {
        int vertex;
        int key;
        Node left, right;
        int npl;

        Node(int vertex, int key) {
            this.vertex = vertex;
            this.key = key;
            this.left = null;
            this.right = null;
            this.npl = 0;
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
        Node min = root;
        root = meld(root.left, root.right);
        return min;
    }

    public boolean isEmpty() {
        return root == null;
    }

    private Node meld(Node h1, Node h2) {
        if (h1 == null) return h2;
        if (h2 == null) return h1;

        if (h1.key > h2.key) {
            Node temp = h1;
            h1 = h2;
            h2 = temp;
        }

        h1.right = meld(h1.right, h2);

        if (h1.left == null) {
            h1.left = h1.right;
            h1.right = null;
        } else {
            if (h1.right != null && h1.left.npl < h1.right.npl) {
                Node temp = h1.left;
                h1.left = h1.right;
                h1.right = temp;
            }
            h1.npl = (h1.right != null ? h1.right.npl : 0) + 1;
        }

        return h1;
    }

    public void decreaseKey(int vertex, int newKey) {
        delete(vertex);
        insert(vertex, newKey);
    }

    private void delete(int vertex) {
        root = delete(root, vertex);
    }

    private Node delete(Node root, int vertex) {
        if (root == null) return null;
        if (root.vertex == vertex) {
            return meld(root.left, root.right);
        } else {
            root.left = delete(root.left, vertex);
            root.right = delete(root.right, vertex);
            return root;
        }
    }
}
