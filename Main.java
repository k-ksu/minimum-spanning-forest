    import java.util.*;

    public class Main {
        public static void main(String[] args) {
            Scanner scanner = new Scanner(System.in);
            int V = scanner.nextInt(); // Number of vertices
            int E = scanner.nextInt(); // Number of edges
            List<Integer> check = new ArrayList<>(Collections.nCopies(V, 0));

            Set<Integer> vertices = new HashSet<>();
            List<Edge<Integer>> edges = new ArrayList<>();

            int scanNumber = 1; // Initialize scan number to 1

            for (int i = 0; i < E; i++) {
                int from = scanner.nextInt();
                int to = scanner.nextInt();
                check.set(from - 1, 1);
                check.set(to - 1, 1);
                int weight = scanner.nextInt();
                vertices.add(from);
                vertices.add(to);
                edges.add(new Edge<>(from, to, weight, scanNumber)); // Initialize scan number for each edge
                scanNumber++; // Increment scan number for the next edge
            }

            List<Tree<Integer>> trees = splitForestIntoTrees(vertices, edges);

            // Sort the trees by scanNumber using bubble sort
            bubbleSort(trees);

            System.out.println(trees.size());
            for (Tree<Integer> tree : trees) {
                // Print K and T
                int K = tree.vertices.size();
                int T = tree.vertices.iterator().next();
                System.out.println(K + " " + T);
                List<Edge<Integer>> mst = minimumSpanningTree(new HashSet<>(tree.vertices), tree.edges);
                for (Edge<Integer> edge : mst) {
                    System.out.println(edge.from + " " + edge.to + " " + edge.weight);
                }
                for (int i = 0; i < check.size(); i++){
                    if (check.get(i) == 0) {
                        int k = i + 1;
                        System.out.println(1 + " " + k);
                    }
                }
            }
        }

        static class Edge<V> {
            V from;
            V to;
            int weight;
            int scanNumber; // Scan number when this edge was created

            Edge(V from, V to, int weight, int scanNumber) {
                this.from = from;
                this.to = to;
                this.weight = weight;
                this.scanNumber = scanNumber;
            }
        }

        static class DSU<V> {
            Map<V, V> parent;

            public DSU(Set<V> vertices) {
                parent = new HashMap<>();
                for (V vertex : vertices) {
                    parent.put(vertex, vertex);
                }
            }

            V find(V x) {
                if (!parent.get(x).equals(x)) {
                    parent.put(x, find(parent.get(x)));
                }
                return parent.get(x);
            }

            void union(V x, V y) {
                parent.put(find(x), find(y));
            }
        }

        static class Tree<V> {
            Set<V> vertices;
            List<Edge<V>> edges;
            int scanNumber;

            Tree(Set<V> vertices, List<Edge<V>> edges) {
                this.vertices = vertices;
                this.edges = edges;
            }
        }

        public static <V> List<Tree<V>> splitForestIntoTrees(Set<V> vertices, List<Edge<V>> edges) {
            List<Tree<V>> trees = new ArrayList<>();
            DSU<V> dsu = new DSU<>(vertices);
            Map<V, Integer> scanNumberMap = new HashMap<>(); // Map to store scan numbers for each tree

            int controlNumber = 1; // Start with control number 1

            for (Edge<V> edge : edges) {
                dsu.union(edge.from, edge.to);
            }

            Map<V, Set<V>> forest = new LinkedHashMap<>(); // Use LinkedHashMap to preserve insertion order
            for (V vertex : vertices) {
                V root = dsu.find(vertex);
                forest.computeIfAbsent(root, k -> new HashSet<>()).add(vertex);
            }

            for (Set<V> tree : forest.values()) {
                Set<Edge<V>> treeEdges = new HashSet<>();
                for (Edge<V> edge : edges) {
                    if (tree.contains(edge.from) && tree.contains(edge.to)) {
                        treeEdges.add(edge);
                    }
                }
                trees.add(new Tree<>(tree, new ArrayList<>(treeEdges)));
                controlNumber++; // Increment control number for the next tree
            }

            // Identify isolated vertices and create separate trees for them
            Set<V> usedVertices = new HashSet<>();
            for (Tree<V> tree : trees) {
                usedVertices.addAll(tree.vertices);
            }
            for (V vertex : vertices) {
                if (!usedVertices.contains(vertex)) {
                    Set<V> isolatedVertex = new HashSet<>();
                    isolatedVertex.add(vertex);
                    trees.add(new Tree<>(isolatedVertex, new ArrayList<>()));
                }
            }

            // Assign the scan number of the first edge added to each tree to all subsequent edges in the same tree
            for (Tree<V> tree : trees) {
                tree.scanNumber = tree.edges.get(0).scanNumber;
            }

            return trees;
        }

        public static <V> List<Edge<V>> minimumSpanningTree(Set<V> vertices, List<Edge<V>> edges) {
            List<Edge<V>> mst = new ArrayList<>();
            DSU<V> dsu = new DSU<>(vertices);

            edges.sort(Comparator.comparingInt(e -> e.weight)); // Sort edges by weight

            for (Edge<V> edge : edges) {
                if (dsu.find(edge.from) != dsu.find(edge.to)) {
                    mst.add(edge);
                    dsu.union(edge.from, edge.to);
                }
            }

            return mst;
        }

        public static void bubbleSort(List<Tree<Integer>> trees) {
            int n = trees.size();
            for (int i = 0; i < n - 1; i++) {
                for (int j = 0; j < n - i - 1; j++) {
                    if (trees.get(j).scanNumber > trees.get(j + 1).scanNumber) {
                        // Swap trees[j] and trees[j+1]
                        Tree<Integer> temp = trees.get(j);
                        trees.set(j, trees.get(j + 1));
                        trees.set(j + 1, temp);
                    }
                }
            }
        }

    }
