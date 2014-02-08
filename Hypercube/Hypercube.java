/**
 * Praca domowa 09 – hypercube
 * Copyright Michał Szczygieł
 * Created at Jan 8, 2014.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class represent architecture for hypercube. Basic operation for this
 * abstract class are creating hypercube with given size. The operation in every
 * node are executed as separated task.
 * 
 * @author Michał Szczygieł <michal.szczygiel@wp.pl>
 * 
 */
public abstract class Hypercube {

    /**
     * Executor for perform operation objects implements {@link Callable}
     */
    private ExecutorService executor;

    /**
     * List contains node, processor.
     */
    List<Node> nodeList;

    /**
     * List of Future interface to collect results from node calculation.
     */
    private List<Future<Double>> results;

    /**
     * This parameter specify the amount of node. Parameter <n-size>
     */
    private Integer size;

    /*
     * The constructor for Hypercube class. Sets size for architecture (amount
     * of node). Set threadPool size and also build hypercube.
     */
    public Hypercube(Integer size) {
        setSize(size);
        setExecutor(Executors.newFixedThreadPool(size));
        buildHypercube(size);
    }

    /**
     * This method builds the hypercube using nodes.
     * 
     * @param size
     *            The amount of nodes in architecture.
     * @return The build hypercube.
     */
    private List<Node> buildHypercube(Integer size) {
        setNodeList(new ArrayList<Node>(size));

        for (int i = 0; i < size; i++) {
            Node node = new Node();
            getNodeList().add(node);
        }

        return nodeList;
    }

    /**
     * This method gets value from operation done by node.
     * 
     * @param node
     *            The node.
     * @return Value of node operation.
     */
    public abstract Double get(Node node);

    /**
     * This method gets executor.
     * 
     * @return the executor
     */
    public ExecutorService getExecutor() {
        return executor;
    }

    /**
     * This method gets node List.
     * 
     * @return the nodeList
     */
    public List<Node> getNodeList() {
        return nodeList;
    }

    /**
     * This method gets result for list of interfaces Future.
     * 
     * @return the results
     */
    public List<Future<Double>> getResults() {
        return results;
    }

    /**
     * This method gets amount of node.
     * 
     * @return the size of hypercube build with nodes.
     */
    public Integer getSize() {
        return size;
    }

    /**
     * This method puts input data to current node given as parameter.
     * 
     * @param node
     *            The node on which put operation should be done.
     * @param input
     *            The data to put.
     */
    public abstract void put(Node node, List<Double> input);

    /**
     * This public method starts all process in hypercube. During this method
     * all nodes performs own calculation.
     * 
     * @throws InterruptedException
     * 
     */
    public void run() throws InterruptedException {
        setResults(executor.invokeAll(nodeList));
        executor.shutdown();
    }

    /**
     * This method sets executor.
     * 
     * @param executor
     *            the executor to set
     */
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * This method sets nodes list.
     * 
     * @param nodeList
     *            the nodeList to set
     */
    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }

    /**
     * This method sets results which contains list of {@link Future}
     * interfaces.
     * 
     * @param results
     *            the results to set
     * 
     */
    public void setResults(List<Future<Double>> results) {
        this.results = results;
    }

    /**
     * This method sets size for hypercube (amount of nodes).
     * 
     * @param size
     *            the size to set
     */
    public void setSize(Integer size) {
        this.size = size;
    }

}
