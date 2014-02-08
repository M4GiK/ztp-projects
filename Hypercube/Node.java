/**
 * Praca domowa 09 – hypercube
 * Copyright Michał Szczygieł
 * Created at Jan 8, 2014.
 */

import java.util.List;
import java.util.concurrent.Callable;

/**
 * This class represent the single node in hypercube architecture.
 * 
 * @author Michał Szczygieł <michal.szczygiel@wp.pl>
 * 
 */
public class Node implements Callable<Double> {

    /**
     * List keeping data to process.
     */
    private List<Double> data;

    /**
     * Variable keeping logic operation value.
     */
    private Boolean isDone = false;

    /**
     * Filed keeping value with local sum for data in node.
     */
    private Double localSum;

    /**
     * This method calculates the sum for data from distributed process. This
     * method overrides an existing method.
     * 
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public Double call() throws Exception {
        setLocalSum(Algorithm.sum(getData()));
        setIsDone(true);
        return getLocalSum();
    }

    /**
     * This method gets local data.
     * 
     * @return the data to perform calculation.
     */
    public List<Double> getData() {
        return data;
    }

    /**
     * This method gets local sum, after perform call() method.
     * 
     * @return the local sum.
     */
    public Double getLocalSum() {
        return localSum;
    }

    /**
     * This method checks if operation for this node is done.
     * 
     * @return the isDone result of operation, if is true operation done
     *         completed, if is not then is still processing or not even
     *         started.
     */
    public Boolean isDone() {
        return isDone;
    }

    /**
     * This method set data to perform for current node.
     * 
     * @param data
     *            the data to set.
     */
    public void setData(List<Double> data) {
        this.data = data;
    }

    /**
     * This method set state for processing.
     * 
     * @param isDone
     *            the isDone to set.
     */
    private void setIsDone(Boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * This method set local sum for node.
     * 
     * @param localSum
     *            the local sum to set.
     */
    private void setLocalSum(Double localSum) {
        this.localSum = localSum;
    }

}
