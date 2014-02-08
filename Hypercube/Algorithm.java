/**
 * Praca domowa 09 – hypercube
 * Copyright Michał Szczygieł
 * Created at Jan 8, 2014.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * This class performs calculation. The connections in hypercube are represents
 * as binary tree.
 * 
 * @author Michał Szczygieł <michal.szczygiel@wp.pl>
 * 
 */
public class Algorithm extends Hypercube {

    /**
     * Left branch of the binary tree.
     */
    private final static Integer LEFT = 0;

    /**
     * Right branch of the binary tree.
     */
    private final static Integer RIGHT = 1;

    /**
     * Chops a list into non-view sublists of length L.
     * 
     * @param list
     *            The list to be chopped.
     * @param L
     *            The size of chop.
     * @return Chopped list.
     */
    static <T> List<List<T>> chopped(List<T> list, final int L) {
        List<List<T>> parts = new ArrayList<List<T>>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<T>(list.subList(i, Math.min(N, i + L))));
        }
        return parts;
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
     * This method perform calculation for count sum.
     * 
     * @param data
     *            The data for count sum.
     * @return The result of sum operation for data in {@link List}.
     */
    public static Double sum(List<Double> data) {
        Double result = 0.0;

        for (Object value : safe(data)) {
            result += Double.parseDouble(value.toString());
        }

        return result;
    }

    /**
     * The fields contains data to perform operations in nodes.
     */
    private List<Double> data;

    /**
     * The constructor for {@link Algorithm}. Invoke constructor in
     * {@link Hypercube} class.
     * 
     * @param data
     *            The data with numbers to calculate sum in hypercube structure.
     * @param size
     *            The size of hypercube, what mean the amount of nodes.
     */
    public Algorithm(List<Double> data, Integer size) {
        super(size);
        setData(data);
        prepareDistrbuteData(data, 0, 0);
    }

    /**
     * 
     * This method gets local sum for current node. This method overrides an
     * existing method.
     * 
     * @see Hypercube#get(java.util.List)
     */
    @Override
    public Double get(Node node) {
        return node.getLocalSum();
    }

    /**
     * This method get data which contains Double numbers.
     * 
     * @return the data
     */
    public List<Double> getData() {
        return data;
    }

    /**
     * This method prepares and distribute data in nodes. The algorithm of
     * propagation data is based on binary tree concept. Distributed operation
     * uses put method to allocate data in nodes. The node 0 (root-node) is
     * communicate with outside world. This algorithm based on level in binary
     * tree and proper identification. Each level(sqrt operation from size of
     * hypercube round up) can be divided into two branch where first have id
     * ex. current and seceond have id current + 2^(level -1).
     * 
     * @param data
     *            The data to propagate in nodes.
     * @param current
     *            The value to recognize proper node.
     * @param level
     *            The value represents level in binary tree.
     */
    private void prepareDistrbuteData(List<Double> data, Integer current,
            Integer level) {
        if ((current + Math.pow(2, level - 1)) < getSize()) {
            List<Double> distributeData = data;
            put(getNodeList().get(current), distributeData);

            if (level <= Math.ceil(Math.sqrt(getSize()))) {
                Integer half = (int) (Math.ceil(getData().size()
                        / Math.pow(2, level)));
                List<List<Double>> parts = Algorithm.chopped(distributeData,
                        half);
                put(getNodeList().get(current), parts.get(LEFT));
                prepareDistrbuteData(parts.get(LEFT), current, level + 1);

                if (parts.size() > 1) {
                    put(getNodeList().get(
                            (int) (current + Math.pow(2, level - 1))),
                            parts.get(RIGHT));
                    prepareDistrbuteData(parts.get(RIGHT),
                            (int) (current + Math.pow(2, level - 1)), level + 1);
                }
            }
        }
    }

    /**
     * This method puts data into current node. This method overrides an
     * existing method.
     * 
     * @see Hypercube#put(java.util.List, java.lang.Integer)
     */
    @Override
    public void put(Node node, List<Double> input) {
        node.setData(input);
    }

    /**
     * This method redistribute data from nodes. The operations is inverse to
     * distribute data ex. in prepareDistrbuteData() method. The algorithm of
     * propagation data is based on binary tree concept. Redistributed operation
     * uses get method to get local sum from nodes. The node 0 (root-node) is
     * communicate with outside world. This algorithm based on level in binary
     * tree and proper identification. Each level(sqrt operation from size of
     * hypercube, round up) can be divided into two branch where first have id
     * ex. current and seceond have id current - 2^(level -1).
     * 
     * @param sum
     *            The value from neighbor contains sum of local sums.
     * @param current
     *            The value to recognize proper node.
     * @param level
     *            The value represents level in binary tree.
     * @return The computed sum from hypercube structure.
     */
    private Double redistributeData(Double sum, Integer current, Integer level) {
        sum = get(getNodeList().get(current));

        if ((current - Math.pow(2, level - 1)) >= 0) {
            redistributeData(sum, current, level + 1);
            sum += redistributeData(sum,
                    (int) (current - Math.pow(2, level - 1)), level);
        }

        return sum;
    }

    /**
     * This method sets data.
     * 
     * @param data
     *            the data to set
     */
    public void setData(List<Double> data) {
        this.data = data;
    }

    /**
     * This method return sum from hypercube operations. The value of sum is a
     * result of redistributed data. The operations are executed in parallel by
     * each node.
     * 
     * @return The sum value, for given data.
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public Double sum() throws InterruptedException, ExecutionException {
        run();
        return redistributeData(0.0, getSize() - 1, 0);
    }

}
