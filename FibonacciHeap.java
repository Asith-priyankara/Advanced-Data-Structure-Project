import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class FibonacciHeap {
    public static class Node {
        int key;
        int data;
        int degree;
        boolean childCut;
        Node parent;
        Node child;
        Node left;
        Node right;

        Node(int key, int data) {
            this.key = key;
            this.data = data;
            this.degree = 0;
            this.childCut = false;
            this.parent = null;
            this.child = null;
            this.left = this;
            this.right = this;
        }
    }

    private Node min;
    private int size;

    public FibonacciHeap() {
        this.min = null;
        this.size = 0;
    }

    public Node insert(int key, int value) {
        Node node = new Node(key, value);
        if (min == null) {
            min = node;
        } else {
            mergeWithRootList(node); // Add the new node to the root list
            if (node.key < min.key) {
                min = node;
            }
        }
        size++;
        return node;
    }

    private void mergeWithRootList(Node node) {
        if(min == null){
            min = node;
        }else{
            node.left = min;
            node.right = min.right;
            min.right.left = node;
            min.right = node;
            if (node.key < min.key) {
                min = node;
            }
        }
    }

    private void removeFromRootList(Node node) {
        if (node.right == node) {
            min = null;
        } else {
            node.left.right = node.right;
            node.right.left = node.left;
            if (min == node) {
                min = node.right;
            }
        }
    }

    private void removeFromChildList(Node parent, Node node) {
        if (node.right == node) {
            parent.child = null;
        } else {
            if (parent.child == node) {
                parent.child = node.right;
            }
            node.left.right = node.right;
            node.right.left = node.left;
        }
        node.left = node.right = node;
    }

    private Node mergeWithChildList(Node child, Node node) {
        if (child == null) {
            return node;
        } else {
            node.left = child;
            node.right = child.right;
            child.right.left = node;
            child.right = node;
        }
        return child;
    }

    public Node extractMin() {
        Node z = min;
        if (z != null) {
            if (z.child != null) {
                for (Node x : iterate(z.child)) {
                    mergeWithRootList(x);
                    x.parent = null;
                }
            }
            removeFromRootList(z);
            if (z == z.right) {
                min = null;
            } else {
                min = z.right;
                consolidate();
            }
            size--;
        }
        return z;
    }

    public void decreaseKey(Node x, int k) {
        if (k > x.key) {
            throw new IllegalArgumentException("New key is greater than current key");
        }

        x.key = k;
        Node y = x.parent;

        if (y != null && x.key < y.key) {
            cut(x, y);
            cascadingCut(y);
        }

        if (x.key < min.key) {
            min = x;
        }
    }


    private void cut(Node x, Node y) {
        removeFromChildList(y, x);
        y.degree--;
        mergeWithRootList(x);
        x.parent = null;
        x.childCut = false;
    }

    private void cascadingCut(Node y) {
        Node z = y.parent;
        if (z != null) {
            if (!y.childCut) {
                y.childCut = true;
            } else {
                cut(y, z);
                cascadingCut(z);
            }
        }
    }

    private List<Node> iterate(Node head) {
        List<Node> nodes = new ArrayList<>();
        if (head == null) return nodes;

        Node current = head;
        do {
            nodes.add(current);
            current = current.right;
        } while (current != head);

        return nodes;
    }

    private void consolidate() {
        int maxDegree = (int) (Math.log(size) / Math.log(2)) + 10;
        List<Node> A = new ArrayList<>(Collections.nCopies(maxDegree, null));

        List<Node> nodes = iterate(min);

        for (Node w : nodes) {
            Node x = w;
            int d = x.degree;
            while (A.get(d) != null) {
                Node y = A.get(d);
                if (x.key > y.key) {
                    Node temp = x;
                    x = y;
                    y = temp;
                }
                link(y, x);
                A.set(d, null);
                d++;
            }
            A.set(d, x);
        }

        min = null;
        for (Node node : A) {
            if (node != null) {
                if (min == null || node.key < min.key) {
                    min = node;
                }
            }
        }
    }

    private void link(Node y, Node x) {
        removeFromRootList(y);
        y.left = y.right = y;
        x.child = mergeWithChildList(x.child, y);
        y.parent = x;
        x.degree++;
        y.childCut = false;
    }

    public boolean isEmpty() {
        return min == null;
    }
}