import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Project Praca domowa 03 – highway.
 * Copyright Michał Szczygieł
 * Created at Oct 30, 2013.
 */

/**
 * 
 * Highway class, gathers information about the positions of the cities on the
 * island, and checks possibility of build highways for the planned
 * construction.
 * 
 * @author Michał Szczygieł <michal.szczygiel@wp.pl>
 * 
 */
public class Highway {

    /**
     * Graph class contain implementation for nodes in the island structure.
     * Every node contain information about localization node in the island
     * structure and also, contain information about highways going out from
     * this node.
     */
    public class Graph {

        /**
         * Field contains maps of nodes to a set contains all adjacent nodes.
         */
        private HashMap<Integer, Set<Integer>> adjacencyMatrix = new HashMap<Integer, Set<Integer>>();

        /**
         * Default constructor for Graph class.
         */
        public Graph() {
        }

        /**
         * Constructor of Graph class. Initialize locations in the island
         * structure as a copy from original source. Stores results in adjacency
         * matrix.
         * 
         * @param original
         *            graph with nodes in the island structure.
         */
        public Graph(Graph original) {
            for (Integer node : original.getNodes()) {
                for (Integer neighbor : original.getAdjacent(node)) {
                    this.addEdge(node, neighbor);
                }
            }
        }

        /**
         * Adds a edge for two nodes. The data also are located in adjacency
         * matrix.
         * 
         * @param node1
         *            Location of node in the island structure.
         * @param node2
         *            Location of node in the island structure.
         */
        public void addEdge(Integer node1, Integer node2) {
            addNode(node1);
            addNode(node2);
            adjacencyMatrix.get(node1).add(node2);
            adjacencyMatrix.get(node2).add(node1);
        }

        /**
         * This method combine two graphs into one new graph.
         * 
         * @param graph1
         *            First graph.
         * @param graph2
         *            Second graph.
         * @return New graph with nodes and edges.
         */
        public Graph addGraphs(Graph graph1, Graph graph2) {
            Graph newGraph = new Graph();

            for (Integer node : graph1.getNodes()) {
                for (Integer neighbor : graph1.getAdjacent(node)) {
                    newGraph.addEdge(node, neighbor);
                }
            }

            for (Integer node : graph2.getNodes()) {
                for (Integer neighbor : graph2.getAdjacent(node)) {
                    newGraph.addEdge(node, neighbor);
                }
            }

            return newGraph;
        }

        /**
         * Adds a node to the graph.
         * 
         * @param node
         */
        private void addNode(Integer node) {
            if (!adjacencyMatrix.containsKey(node)) {
                adjacencyMatrix.put(node, new HashSet<Integer>());
            }
        }

        /**
         * Checks if connected graph is a path.
         * 
         * @param graph
         * @return true if is a path, or false if is not.
         */
        public boolean checkPath(Graph graph) {
            Integer endPoints = 0;

            for (Integer node : graph.getNodes()) {
                Integer degree = graph.getDegree(node);
                if (degree == 1) {
                    endPoints++;
                } else if (degree != 2) {
                    return false;
                }
            }

            if (endPoints != 2) {
                return false;
            }

            return true;
        }

        /**
         * Gets all adjacent nodes for current node.
         * 
         * @param node
         * @return A set contains all node's adjacency.
         */
        public Set<Integer> getAdjacent(Integer node) {
            return adjacencyMatrix.get(node);
        }

        /**
         * This method returns the number of neighbors for current node.
         * 
         * @param node
         * @return the quantity of neighbors for node, or -1 if does not.
         */
        private Integer getDegree(Integer node) {
            if (adjacencyMatrix.containsKey(node)) {
                return adjacencyMatrix.get(node).size();
            } else {
                return -1;
            }
        }

        /**
         * @return set of nodes containing in the graph.
         */
        public Set<Integer> getNodes() {
            return adjacencyMatrix.keySet();
        }

        /**
         * Checks whether the two nodes are connected
         * 
         * @param node1
         *            Location of node in the island structure.
         * @param node2
         *            Location of node in the island structure.
         * @return true if are connected, false if are not.
         */
        public boolean hasEdge(Integer node1, Integer node2) {
            return adjacencyMatrix.containsKey(node1)
                    && adjacencyMatrix.get(node1).contains(node2);
        }

        /**
         * This method check if existing node is in adjacency matrix.
         * 
         * @param node
         * @return true if containing the node, false if is not.
         */
        public boolean hasNode(Integer node) {
            return adjacencyMatrix.containsKey(node);
        }

        /**
         * @return Amount of edges in the graph.
         */
        public Integer quantityEdges() {
            Integer quantity = 0;

            for (Set<Integer> edges : adjacencyMatrix.values()) {
                quantity += edges.size();
            }

            return quantity / 2;
        }

        /**
         * @return Amount of nodes in the graph.
         */
        public Integer quantityNode() {
            return adjacencyMatrix.size();
        }

        /**
         * Removes edge from two nodes in adjacency matrix.
         * 
         * @param node1
         *            Location of node in the island structure.
         * @param node2
         *            Location of node in the island structure.
         */
        public void removeEdge(Integer node1, Integer node2) {
            if (hasEdge(node1, node2) && hasEdge(node2, node1)) {
                adjacencyMatrix.get(node1).remove(node2);
                adjacencyMatrix.get(node2).remove(node1);

                if (adjacencyMatrix.get(node1).size() == 0) {
                    adjacencyMatrix.remove(node1);
                }

                if (adjacencyMatrix.get(node2).size() == 0) {
                    adjacencyMatrix.remove(node2);
                }
            }
        }
    }

    /**
     * Traversal class contains methods for traversing a graph.
     */
    public class Traversal {

        /**
         * Map contains colors for graph.
         */
        private Map<Integer, Integer> colors;

        /**
         * HashSet to mark nodes which already cycled in current traversal.
         */
        private Set<Integer> cycled = new HashSet<Integer>();

        /**
         * Object of graph to make operation on it.
         */
        private Graph graph;

        /**
         * Next node in traversal.
         */
        private Integer nextNode;

        /**
         * Previous node in traversal.
         */
        private Integer previousNode;

        /**
         * Variable contains the result for graph.
         */
        private Graph result;

        /**
         * Target for node.
         */
        private Integer target;

        /**
         * Constructor.
         * 
         * @param graph
         */
        public Traversal(Graph graph) {
            this.graph = graph;
        }

        /**
         * Method makes pieces of roads combine.
         * 
         * @param cycle
         *            The current cycle into graph.
         * @param node
         */
        private void createPiece(Graph cycle, Integer node) {
            if (!cycle.hasNode(node)) {
                cycled.add(node);

                for (Integer neighbor : graph.getAdjacent(node)) {
                    if (!result.hasEdge(neighbor, node)) {
                        result.addEdge(node, neighbor);
                        createPiece(cycle, neighbor);
                    }
                }
            }
        }

        /**
         * Method finds any cycle in graph.
         * 
         * @return
         */
        public Graph findCycle() {
            cycled.clear();
            result = new Graph();
            target = graph.getNodes().iterator().next();

            return findCycle(target);

        }

        /**
         * Method finding cycle from current node.
         * 
         * @param node
         *            The current node
         * @return A cycle
         */
        private Graph findCycle(Integer node) {
            cycled.add(node);

            for (Integer neighbor : graph.getAdjacent(node)) {
                if (neighbor.equals(target) && result.quantityNode() > 2) {
                    result.addEdge(node, neighbor);
                    return result;
                } else if (!cycled.contains(neighbor)) {
                    result.addEdge(node, neighbor);
                    Graph completedCycle = findCycle(neighbor);

                    if (completedCycle != null) {
                        return completedCycle;
                    }

                    result.removeEdge(node, neighbor);
                }
            }

            return null;
        }

        /**
         * This method finds path.
         * 
         * @param node
         *            Current node in the path.
         * @return true if the path was found, or false if was not.
         */
        private boolean findPath(Integer node) {
            cycled.add(node);

            for (Integer neighbor : graph.getAdjacent(node)) {
                if (neighbor.equals(target)) {
                    result.addEdge(node, neighbor);
                    return true;
                } else if (!cycled.contains(neighbor)) {
                    result.addEdge(node, neighbor);
                    if (findPath(neighbor)) {
                        return true;
                    }
                    result.removeEdge(node, neighbor);
                }
            }
            return false;
        }

        /**
         * Method finds path between two nodes into the graph.
         * 
         * @param startNode
         *            The starting node.
         * @param endNode
         *            The ending node.
         * @param prohibited
         *            Collection of path which can not pass through.
         * @return The path between two nodes.
         */
        public Graph findPath(Integer startNode, Integer endNode,
                Collection<Integer> prohibited) {
            cycled.clear();
            cycled.addAll(prohibited);
            result = new Graph();
            target = endNode;

            return findPath(startNode) ? result : null;
        }

        /**
         * Make round in cycle.
         * 
         * @return the next node in cycle.
         */
        public Integer getCycle() {
            if (nextNode == null) {
                previousNode = graph.getNodes().iterator().next();
                nextNode = graph.getAdjacent(previousNode).iterator().next();
            } else {
                for (Integer neighbor : graph.getAdjacent(nextNode)) {
                    if (!neighbor.equals(previousNode)) {
                        previousNode = nextNode;
                        nextNode = neighbor;
                        break;
                    }
                }
            }
            return previousNode;
        }

        /**
         * This method check if the graph is duplex or not.
         * 
         * @return True if graph is duplex, or false if not.
         */
        public boolean isDuplex() {
            if (graph.quantityNode() == 0) {
                return true;
            }

            colors = new HashMap<Integer, Integer>();
            return isDuplex(graph.getNodes().iterator().next(), true);
        }

        /**
         * Checks if graph is duplex for current node.
         * 
         * @param node
         *            The current node.
         * @param color
         *            To coloring node.
         * @return True if method do not found conflicts, or false is found.
         */
        private boolean isDuplex(Integer node, boolean color) {
            if (colors.containsKey(node)) {
                if (!colors.get(node).equals(color ? 1 : 0)) {
                    return false;
                } else {
                    return true;
                }
            } else {
                colors.put(node, color ? 1 : 0);
                boolean duplex = true;

                for (Integer neighbor : graph.getAdjacent(node)) {
                    duplex = duplex && isDuplex(neighbor, !color);
                }

                return duplex;
            }
        }

        /**
         * This method split the graph into pieces using the cycle. Splits the
         * graph in current cycle into pieces.
         * 
         * @param cycle
         *            The current cycle into graph.
         * @return Set which containing all pieces of the graph.
         */
        public Set<Graph> splitIntoPieces(Graph cycle) {
            cycled.clear();
            Set<Graph> pieces = new HashSet<Graph>();

            for (Integer node : cycle.getNodes()) {
                cycled.add(node);
                for (Integer neighbor : graph.getAdjacent(node)) {
                    if (!cycled.contains(neighbor)
                            && !cycle.hasEdge(neighbor, node)) {
                        result = new Graph();
                        result.addEdge(node, neighbor);
                        createPiece(cycle, neighbor);
                        pieces.add(result);
                    }
                }
            }
            return pieces;
        }
    }

    /**
     * Object contains nodes for the island structure.
     */
    private Graph graph = new Graph();

    /**
     * Set contains representation for non interlaced graph of length four.
     */
    private final Set<String> notInterlacedSet = nonInterlaced();

    /**
     * Variable contains amount of nodes.
     */
    private Integer quantityOfNodes;

    /**
     * Contains information about status for build highways. If return 0, it is
     * impossible to build highways other case, return 1.
     */
    private Integer status = 1;

    /**
     * Constructor. The core of problem. Initializes graph with edges what mean
     * cities with highways. Also set status for possibility to build highways.
     * 
     * @param locations
     */
    public Highway(ArrayList<Integer> locations) {
        setQuantityOfNodes(locations.get(0));

        if (getQuantityOfNodes() > 2 && locations.size() > 3
                && locations.get(1) > 1) {

            creatNodes(getQuantityOfNodes());

            for (int i = 2; i < locations.size(); i += 2) {
                if (locations.get(i) <= getQuantityOfNodes()
                        && locations.get(i) > 0
                        && locations.get(i + 1) <= getQuantityOfNodes()
                        && locations.get(i + 1) > 0) {
                    graph.addEdge(locations.get(i), locations.get(i + 1));
                } else {
                    this.status = 0;
                }
            }

            try {
                Graph cycle = (new Traversal(graph)).findCycle();

                if (!checkPlanarity(graph, cycle)) {
                    this.status = 0;
                }
            } catch (StackOverflowError e) {
                System.out.println("Wynik : 1");
                System.exit(1);
            }

        } else {
            this.status = 0;
        }

    }

    /**
     * This method checks the planarity of graph. Method is described in
     * "Graph Drawing: Algorithms for the Visualization of Graphs" book written
     * by Ioannis G. Tollis, Giuseppe Di Battista, Peter Eades, Roberto
     * Tamassia.
     * 
     * @param graph
     * @param cycle
     * @return true if the graph is planar, or false is not.
     */
    private boolean checkPlanarity(Graph graph, Graph cycle) {
        if (graph.quantityEdges() > 3 * graph.quantityNode() - 6) {
            return false;
        }

        Set<Graph> pieces = (new Traversal(graph)).splitIntoPieces(cycle);

        if (!createPieces(pieces, graph, cycle)) {
            return false;
        }

        return interlacement(pieces, cycle);
    }

    /**
     * This method create and check if are cycled.
     * 
     * @param pieces
     *            Set to check.
     * @param graph
     *            The main graph
     * @param cycle
     *            The cycle of main graph.
     * @return True if is still planarity, or false if is not.
     */
    private boolean createPieces(Set<Graph> pieces, Graph graph, Graph cycle) {
        for (Graph piece : pieces) {
            if (!graph.checkPath(piece)) {

                Integer startNode = null;

                for (Integer node : cycle.getNodes()) {
                    if (piece.hasNode(node)) {
                        startNode = node;
                        break;
                    }
                }

                Graph cycleSegment = new Graph(cycle);
                Integer previousNode = startNode;
                Integer currentNode = cycle.getAdjacent(previousNode)
                        .iterator().next();

                cycleSegment.removeEdge(previousNode, currentNode);

                while (!piece.hasNode(currentNode)) {
                    for (Integer node : cycle.getAdjacent(currentNode)) {
                        if (!node.equals(previousNode)) {
                            previousNode = currentNode;
                            currentNode = node;
                            break;
                        }
                    }
                    cycleSegment.removeEdge(previousNode, currentNode);
                }

                Integer endNode = currentNode;

                Traversal traversal = new Traversal(piece);
                Graph piecePath = traversal.findPath(startNode, endNode,
                        cycle.getNodes());

                Graph pieceGraph = graph.addGraphs(cycle, piece);
                Graph cycleGraph = graph.addGraphs(cycleSegment, piecePath);

                if (!checkPlanarity(pieceGraph, cycleGraph)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * This method initializing graph with basic connections of nodes what mean
     * cities with roads, and natural border - sea;
     * 
     * @param quantity
     *            of cities
     */
    private void creatNodes(Integer quantity) {
        for (int location = 1; location < quantity; location++) {
            graph.addEdge(location, location + 1);
        }
        graph.addEdge(1, quantity);
    }

    /**
     * Fills list of chars for graph.
     * 
     * @param cycle
     * @param combine
     * @param pieceArray
     * @param i
     */
    private void fillListOfChar(Graph cycle, Graph combine,
            Object[] pieceArray, int i) {
        Graph testGraph = (Graph) pieceArray[i];

        for (int j = i + 1; j < pieceArray.length; j++) {
            Graph compareGraph = (Graph) pieceArray[j];
            Traversal traversal = new Traversal(cycle);
            String listofChar = new String();
            Integer quantityBoth = 0;
            char lastChar = ' ';

            for (int k = 0; k < cycle.quantityNode(); k++) {
                Integer node = traversal.getCycle();

                if (testGraph.hasNode(node) && compareGraph.hasNode(node)) {
                    quantityBoth++;
                    listofChar += 'b';
                    lastChar = 'b';
                } else if (testGraph.hasNode(node) && lastChar != 't') {
                    listofChar += 't';
                    lastChar = 't';
                } else if (compareGraph.hasNode(node) && lastChar != 'c') {
                    listofChar += 'c';
                    lastChar = 'c';
                }
            }

            if ((lastChar == 't' || lastChar == 'c')
                    && listofChar.charAt(0) == lastChar) {
                listofChar = listofChar.substring(1);
            }

            boolean interlaced = false;

            if (listofChar.length() > 4 || quantityBoth > 2) {
                interlaced = true;
            } else if (listofChar.length() == 4
                    && !notInterlacedSet.contains(listofChar)) {
                interlaced = true;
            }

            if (interlaced) {
                combine.addEdge(i, j);
            }
        }
    }

    /**
     * @return the quantityOfNodes
     */
    public Integer getQuantityOfNodes() {
        return quantityOfNodes;
    }

    /**
     * @return the status of possibility to build highways. If return 0, it is
     *         impossible to build highways other case, return 1.
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * This method check if possible interlacement for graph.
     * 
     * @param pieces
     *            Set to check.
     * @param cycle
     *            The cycle of main graph.
     */
    private boolean interlacement(Set<Graph> pieces, Graph cycle) {
        Graph combine = new Graph();
        Object[] pieceArray = pieces.toArray();

        for (int i = 0; i < pieceArray.length; i++) {
            fillListOfChar(cycle, combine, pieceArray, i);
        }

        return (new Traversal(combine)).isDuplex();
    }

    /**
     * Method creates non interlaces set for graph.
     * 
     * @return The non interlaced set.
     */
    private Set<String> nonInterlaced() {
        Set<String> nonInterlaced = new HashSet<String>();
        String[] nonInterlacedStrings = { "tbcb", "btbc", "bcbt", "cbtb" };

        for (String string : nonInterlacedStrings) {
            nonInterlaced.add(string);
        }

        return nonInterlaced;
    }

    /**
     * @param quantityOfNodes
     *            the quantityOfNodes to set
     */
    public void setQuantityOfNodes(Integer quantityOfNodes) {
        this.quantityOfNodes = quantityOfNodes;
    }
}
