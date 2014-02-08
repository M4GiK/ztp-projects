/**
 * Project Praca domowa 010 – path
 * Copyright Michał Szczygieł
 * Created at Jan 17, 2014.
 */
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * This class represents calculation for founding the most optional route in
 * graph. The search algorithm is based on Dynic algorithm in O(V^2*E).
 * 
 * @author Michał Szczygieł <michal.szczygiel@wp.pl>
 * 
 */
public class Path {

    /**
     * This class represents edge into graph.
     */
    public class Edge {

        /**
         * This field represents the bandwidth for current edge.
         */
        private Float capacity;

        /**
         * This filed represents value for max flow, based on Dynic algorithm.
         */
        private Float flow = 0f;

        /**
         * This field describe the starting point in the edge.
         */
        private Integer x;

        /**
         * This field describe the ending point in the edge.
         */
        private Integer y;

        /*
         * The constructor of edge. Sets node and capacity.
         */
        public Edge(Integer x, Integer y, Float capacity) {
            setX(x);
            setY(y);
            setCapacity(capacity);
        }

        /**
         * This method gets capacity of edge.
         * 
         * @return the capacity
         */
        public Float getCapacity() {
            return capacity;
        }

        /**
         * This method gets flow for edge.
         * 
         * @return the flow
         */
        public Float getFlow() {
            return flow;
        }

        /**
         * This method gets starting point for this edge.
         * 
         * @return the x
         */
        public Integer getX() {
            return x;
        }

        /**
         * This method gets ending points in the edge.
         * 
         * @return the y
         */
        public Integer getY() {
            return y;
        }

        /**
         * This method sets capacity for edge.
         * 
         * @param capacity
         *            the capacity to set
         */
        public void setCapacity(Float capacity) {
            this.capacity = capacity;
        }

        /**
         * This method sets flow for this edge.
         * 
         * @param flow
         *            the flow to set
         */
        public void setFlow(Float flow) {
            this.flow = flow;
        }

        /**
         * This method sets starting point for this edge.
         * 
         * @param x
         *            the x to set
         */
        public void setX(Integer x) {
            this.x = x;
        }

        /**
         * This method sets ending point for this edge.
         * 
         * @param y
         *            the y to set
         */
        public void setY(Integer y) {
            this.y = y;
        }

    }

    /**
     * This method perform safe operations on the lists.
     * 
     * @param list
     *            The list to check if is empty.
     * @return safe list, which avoid {@link NullPointerException}
     */
    @SuppressWarnings("rawtypes")
    public static List safe(List list) {
        return list == null ? Collections.EMPTY_LIST : list;
    }

    /**
     * The array with the most optional paths.
     */
    private ArrayList<ArrayList<Edge>> foundPaths = new ArrayList<ArrayList<Edge>>();

    /**
     * The list which represents current graph and neighborhood matrix.
     */
    private List<Edge>[] graph;

    /**
     * The minimal transportation cost for graph.
     */
    private Float minimalTransportationCost = Float.MAX_VALUE;

    /**
     * List of nodes with reached path to destination
     */
    private ArrayList<Edge> path;

    /**
     * The start point for routing.
     */
    private final Integer START_POINT = 1;

    /**
     * The constructor for {@link Path}, builds graph.
     * 
     * @param retrievedData
     *            The retrieved data from database.
     */
    public Path(Map<Point2D, Float> retrievedData) {
        buildGraph(retrievedData);
    }

    /**
     * This method adds edge.
     * 
     * @param graph
     * @param source
     * @param traversal
     * @param capacity
     */
    public void addEdge(List<Edge>[] graph, Integer source, Integer traversal,
            Float capacity) {
        graph[source]
                .add(new Edge(traversal, graph[traversal].size(), capacity));
        graph[traversal].add(new Edge(source, graph[source].size() - 1, 0f));
    }

    /**
     * This method build the graph.
     * 
     * @param retrievedData
     */
    private void buildGraph(Map<Point2D, Float> retrievedData) {
        graph = prepareGraph(retrievedData.size() * 2);

        for (Entry<Point2D, Float> data : retrievedData.entrySet()) {
            addEdge(graph, (int) data.getKey().getX(), (int) data.getKey()
                    .getY(), data.getValue());
            addEdge(graph, (int) data.getKey().getY(), (int) data.getKey()
                    .getX(), data.getValue());
        }
    }

    /**
     * Breadth-first search algorithm. In graph theory, breadth-first search
     * (BFS) is a strategy for searching in a graph when search is limited to
     * essentially two operations: (a) visit and inspect a node of a graph; (b)
     * gain access to visit the nodes that neighbor the currently visited node.
     * The BFS begins at a root node and inspects all the neighboring nodes.
     * Then for each of those neighbor nodes in turn, it inspects their neighbor
     * nodes which were unvisited, and so on. Compare BFS with the equivalent,
     * but more memory-efficient Iterative deepening depth-first search and
     * contrast with depth-first search.
     * 
     * @param graph
     * @param source
     * @param destination
     * @param distinct
     * @return logic value if is true is still traversing, or false if end.
     */
    private boolean dinicBfs(List<Edge>[] graph, Integer source,
            Integer destination, int[] distinct) {
        Arrays.fill(distinct, -1);
        distinct[source] = 0;
        int[] queue = new int[graph.length];
        Integer sizeQueue = 0;
        queue[sizeQueue++] = source;

        for (int i = 0; i < sizeQueue; i++) {
            Integer u = queue[i];
            for (Edge edge : graph[u]) {

                if (distinct[edge.getX()] < 0
                        && edge.getFlow() < edge.getCapacity()) {
                    distinct[edge.getX()] = distinct[u] + 1;
                    queue[sizeQueue++] = edge.getX();
                }
            }
        }
        // System.out.println(sizeQueue);

        return distinct[destination] >= 0;
    }

    /**
     * Depth-first search (DFS) is an algorithm for traversing or searching tree
     * or graph data structures. One starts at the root (selecting some
     * arbitrary node as the root in the case of a graph) and explores as far as
     * possible along each branch before backtracking.
     * 
     * @param graph
     * @param ptr
     * @param distinct
     * @param destination
     * @param source
     * @param maxFlow
     * @return
     */
    private Float dinicDfs(List<Edge>[] graph, int[] ptr, int[] distinct,
            Integer destination, Integer u, Float maxFlow) {
        if (u == destination) {
            return maxFlow;
        }

        for (; ptr[u] < graph[u].size(); ++ptr[u]) {
            Edge edge = graph[u].get(ptr[u]);

            if (distinct[edge.getX()] == distinct[u] + 1
                    && edge.getFlow() < edge.getCapacity()) {
                Float deepFirst = dinicDfs(graph, ptr, distinct, destination,
                        edge.getX(),
                        Math.min(maxFlow, edge.getCapacity() - edge.getFlow()));
                if (deepFirst > 0) {
                    path.add(edge);
                    edge.setFlow(edge.getFlow() + deepFirst);
                    graph[edge.getX()].get(edge.getY()).setFlow(
                            graph[edge.getX()].get(edge.getY()).getFlow()
                                    - deepFirst);

                    return deepFirst;
                }
            }
        }
        path.add(null);

        return (float) 0;
    }

    /**
     * This method counts maximum flow.
     * 
     * @param graph
     * @param source
     * @param destination
     */
    private void maxFlow(List<Edge>[] graph, Integer source, Integer destination) {
        int[] distinct = new int[graph.length];
        while (dinicBfs(graph, source, destination, distinct)) {
            int[] ptr = new int[graph.length];
            while (true) {
                path = new ArrayList<Edge>();
                Float deepFirst = dinicDfs(graph, ptr, distinct, destination,
                        source, Float.MAX_VALUE);
                foundPaths.add(path);

                if (deepFirst == 0) {
                    break;
                }
            }
        }
    }

    /**
     * This method returns the lowest optimal cost for routing in graph.
     * 
     * @param index
     *            Destination point.
     * @return The lowest cost for routing in graph
     */
    public Float optimalTransportationCost(Integer index) {
        maxFlow(graph, START_POINT, index);

        Float minCost = .0f;
        for (ArrayList<Edge> paths : foundPaths) {

            if (paths.size() > 1) {
                Collections.reverse(paths);
                Float temp = .0f;
                Float node = 0f;
                Boolean wasIteration = false;

                for (Object object : safe(paths)) {
                    Edge edge = (Edge) object;

                    if (edge != null) {
                        if (node == 0) {
                            node = edge.getCapacity();
                            temp = node;
                        } else {
                            minCost += Math.abs(edge.getCapacity() - temp);
                            temp = (float) edge.getCapacity();
                            wasIteration = true;
                        }

                    }
                }

                if (minCost < minimalTransportationCost && wasIteration) {
                    minimalTransportationCost = minCost;
                } else {
                    minCost = 0f;
                }
            }
        }
        return minimalTransportationCost;
    }

    /**
     * This method using given size, prepare an empty ArrayList of ArrayList of
     * Edges.
     * 
     * @param size
     *            The size to create empty ArrayList
     * @return Empty ArrayList of List of edges.
     */
    private List<Edge>[] prepareGraph(Integer size) {
        @SuppressWarnings("unchecked")
        List<Edge>[] graph = (List<Edge>[]) new List<?>[size];

        for (int i = 0; i < size; i++) {
            graph[i] = new ArrayList<Edge>();
        }

        return graph;
    }

}
