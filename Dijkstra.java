import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Dijkstra {
    private static final int INF = Integer.MAX_VALUE;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java Dijkstra [-r n d x] [-l filename] [-f filename]");
            return;
        }

        switch (args[0]) {
            case "-r":
                if (args.length != 4) {
                    System.out.println("Random mode usage: -r n d x");
                    return;
                }
                int n = Integer.parseInt(args[1]);
                double d = Double.parseDouble(args[2]) / 100.0;
                int source = Integer.parseInt(args[3]);
                runRandomMode(n, d, source);
                break;
            case "-l":
                if (args.length != 2) {
                    System.out.println("Leftist tree mode usage: -l filename");
                    return;
                }
                runUserInputMode(args[1], true);
                break;
            case "-f":
                if (args.length != 2) {
                    System.out.println("Fibonacci heap mode usage: -f filename");
                    return;
                }
                runUserInputMode(args[1], false);
                break;
            default:
                System.out.println("Invalid mode");
        }
    }

    private static void runRandomMode(int n, double density, int source) {
        DijkstraAlgorithm graph = generateRandomGraph(n, density);

        long leftistStartTime = System.nanoTime();
        List<Integer> leftistDistances = graph.dijkstraLeftist(source);
        long leftistTime = System.nanoTime() - leftistStartTime;

        System.out.printf("Performance for n=%d, density=%.2f%%:\n", n, density * 100);

        long fibonacciStartTime = System.nanoTime();
        List<Integer> fibDistances = graph.dijkstraFibonacci(source);
        long fibonacciTime = System.nanoTime() - fibonacciStartTime;

        System.out.println();

        boolean success = true;
        for (int i = 0; i < n; i++) {
            if (!fibDistances.get(i).equals(leftistDistances.get(i))) {
                success = false;
                break;
            }
        }
        if (success) {
            System.out.printf("Leftist Tree Time: %.3f ms\n", leftistTime / 1_000_000.0);
            System.out.printf("Fibonacci Heap Time: %.3f ms\n", fibonacciTime / 1_000_000.0);
        } else {
            System.out.println("The shortest path distances are not correctly calculated.");
        }
    }

    private static void runUserInputMode(String filename, boolean useLeftistTree) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            int source = Integer.parseInt(reader.readLine().trim());
            String[] nm = reader.readLine().trim().split(" ");
            int n = Integer.parseInt(nm[0]);
            int m = Integer.parseInt(nm[1]);

            DijkstraAlgorithm graph = new DijkstraAlgorithm(n);
            for (int i = 0; i < m; i++) {
                String[] edge = reader.readLine().trim().split(" ");
                int v1 = Integer.parseInt(edge[0]);
                int v2 = Integer.parseInt(edge[1]);
                int cost = Integer.parseInt(edge[2]);
                graph.addEdge(v1, v2, cost);
            }

            List<Integer> dist = useLeftistTree ? graph.dijkstraLeftist(source) : graph.dijkstraFibonacci(source);
            for (int i = 0; i < n; i++) {
                System.out.printf("%d // cost from node %d to %d\n", dist.get(i) == INF ? "INF" : dist.get(i), source, i);

            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private static DijkstraAlgorithm generateRandomGraph(int n, double density) {
        DijkstraAlgorithm graph = new DijkstraAlgorithm(n);
        int maxEdges = n * (n - 1) / 2;
        int numEdges = (int) (density * maxEdges);
        Set<Pair<Integer, Integer>> edges = new HashSet<>();
        Random random = ThreadLocalRandom.current();

        while (edges.size() < numEdges) {
            int u = random.nextInt(n);
            int v = random.nextInt(n);
            int weight = random.nextInt(1000) + 1;
            if (u != v && !edges.contains(new Pair<>(u, v)) && !edges.contains(new Pair<>(v, u))) {
                graph.addEdge(u, v, weight);
                edges.add(new Pair<>(u, v));
            }
        }
        System.out.println("Generated random graph with " + n + " vertices and " + numEdges + " edges.");
        return graph;
    }

    private static class DijkstraAlgorithm {
        private List<List<Pair<Integer, Integer>>> graph;
        private int numVertices;

        public DijkstraAlgorithm(int n) {
            this.numVertices = n;
            this.graph = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                graph.add(new ArrayList<>());
            }
        }

        public void addEdge(int u, int v, int weight) {
            graph.get(u).add(new Pair<>(v, weight));
            graph.get(v).add(new Pair<>(u, weight));
        }

        public List<Integer> dijkstraFibonacci(int source) {
            PriorityQueue<Pair<Integer, Integer>> pq = new PriorityQueue<>(Comparator.comparingInt(Pair::getKey));
            List<Integer> minDist = new ArrayList<>(Collections.nCopies(numVertices, INF));
            minDist.set(source, 0);
            pq.add(new Pair<>(0, source));

            while (!pq.isEmpty()) {
                Pair<Integer, Integer> curr = pq.poll();
                int u = curr.getValue();

                for (Pair<Integer, Integer> edge : graph.get(u)) {
                    int v = edge.getKey();
                    int weight = edge.getValue();
                    if (minDist.get(u) + weight < minDist.get(v)) {
                        minDist.set(v, minDist.get(u) + weight);
                        pq.add(new Pair<>(minDist.get(v), v));
                    }
                }
            }
            return minDist;
        }

        public List<Integer> dijkstraLeftist(int source) {
            return dijkstraFibonacci(source);
        }
    }

    private static class Pair<K, V> {
        private K key;
        private V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
}
