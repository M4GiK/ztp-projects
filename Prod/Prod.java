/**
 * Project Praca domowa 05 – prod.
 * Copyright Michał Szczygieł
 * Created at Nov 22, 2013.
 */
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 
 * Class responsible for division of labor. Instance of class using 3 Threads,
 * one producer and two consumer.
 * 
 * @author Michał Szczygieł <michal.szczygiel@wp.pl>
 * 
 */
public class Prod {

    /**
     * This class represents the consumer, which means that it retrieves data
     * that processed the manufacturer. This class also counts the average for
     * serviced integers.
     */
    private class Consumer extends Worker {

        /**
         * The amount of elements, which was processed.
         */
        private Integer quantity = new Integer(0);

        /**
         * Variable responsible for getting correct data from queue.
         */
        private boolean rangeOverTen;

        /**
         * The sum of elements, which was processed.
         */
        private Integer sum = new Integer(0);

        /**
         * The constructor for {@link Consumer}.
         * 
         * @param queue
         *            To set reference the {@link Queue} class.
         * @param mean
         *            The average for normal distribution.
         * @param sigma
         *            The sigma value for normal distribution.
         * @param rangeOverTen
         *            The value responsible for getting correct data from queue.
         */
        public Consumer(Queue queue, Double mean, Double sigma,
                boolean rangeOverTen) {
            this.queue = queue;
            this.mean = mean;
            this.sigma = sigma;
            this.rangeOverTen = rangeOverTen;
        }

        /**
         * This method calculate mean value for processed integers.
         * 
         * @return The avarage value for processed data.
         */
        public double meanValue() {
            if (quantity == 0) {
                return 0.0;
            } else {
                return (double) sum / quantity;
            }
        }

        /**
         * This method retrieve the data from queue and counts the processed
         * integers. This method overrides an existing method.
         * 
         * @see java.lang.Thread#run()
         */
        public void run() {
            while (producer.isAvailable() || !queue.isHandledEmpty()) {
                Integer result = queue.get(rangeOverTen);

                if (result != null) {
                    quantity++;
                    sum += result;
                }

                try {
                    sleep(normalDistribution(mean, sigma));
                } catch (InterruptedException e) {
                }
            }
            setAvailable(false);
        }
    }

    /**
     * This class represents the producer, which retrieves data from file and
     * puts to the queue, which should be handled by consumers.
     */
    private class Producer extends Worker {

        /**
         * The constructor for {@link Producer}.
         * 
         * @param queue
         *            To set reference the {@link Queue} class.
         * @param mean
         *            The average for normal distribution.
         * @param sigma
         *            The sigma value for normal distribution.
         */
        public Producer(Queue queue, Double mean, Double sigma) {
            this.queue = queue;
            this.mean = mean;
            this.sigma = sigma;
        }

        /**
         * This method retrieve the data from file and puts to the queue, which
         * should be handled by consumers. This method overrides an existing
         * method.
         * 
         * @see java.lang.Thread#run()
         */
        public void run() {
            while (!queue.isEmpty()) {
                queue.put();
                try {
                    sleep(normalDistribution(mean, sigma));
                } catch (InterruptedException e) {
                }
            }
            setAvailable(false);
        }
    }

    /**
     * Class represents the queue. This class contains method for getting and
     * putting elements to the queue.
     */
    private class Queue {

        /**
         * The data to be processed by {@link Producer}.
         */
        private LinkedList<Integer> queue;

        /**
         * Synchronized collection of integers.
         */
        private List<Integer> queueHandled = Collections
                .synchronizedList(new LinkedList<Integer>());

        /**
         * The constructor for {@link Queue} class.
         * 
         * @param queue
         *            Data from file to processed.
         */
        public Queue(LinkedList<Integer> queue) {
            this.queue = queue;
        }

        /**
         * This method gets integer from queue.
         * 
         * @param rangeOverTen
         *            Value responsible for getting correct data from queue.
         * @return Integer from queue.
         */
        public Integer get(boolean rangeOverTen) {
            synchronized (queueHandled) {
                return getValidObject(queueHandled, rangeOverTen);
            }
        }

        /**
         * This method gets correct value from queue, depends from queueHandled.
         * If queueHandled is set as true, then return values >= 10, otherwise
         * values <= 10.
         * 
         * @param queueHandled
         *            The queue to processed.
         * @param rangeOverTen
         *            Value responsible for getting correct data from queue.
         * @return
         */
        private Integer getValidObject(List<Integer> queueHandled,
                boolean rangeOverTen) {
            synchronized (queueHandled) {
                for (Integer integer : queueHandled) {
                    if (rangeOverTen) {
                        if (integer >= 10) {
                            queueHandled.remove(integer);
                            return integer;
                        }
                    } else {
                        if (integer <= 10) {
                            queueHandled.remove(integer);
                            return integer;
                        }
                    }
                }
            }
            return null;
        }

        /**
         * This method checks if all data from file was processed.
         * 
         * @return True if all data was processed.
         */
        public boolean isEmpty() {
            boolean isEmpty = false;

            if (queue.size() == 0) {
                isEmpty = true;
            }

            return isEmpty;
        }

        /**
         * This method checks if all data from queue was processed.
         * 
         * @return True if all data was processed.
         */
        public boolean isHandledEmpty() {
            boolean isEmpty = false;

            if (queueHandled.size() == 0) {
                isEmpty = true;
            }

            return isEmpty;
        }

        /**
         * This method adds element to the queue and also removes current
         * element from list which contains data from file.
         * 
         * @return true.
         */
        public boolean put() {
            synchronized (queueHandled) {
                if (queueHandled.size() >= getSize()) {
                    return true;
                }

                queueHandled.add(queueHandled.size(), queue.removeFirst());
                return true;
            }
        }
    }

    /**
     * Abstract class represents the worker. This class extends the
     * {@link Thread} for thread processing.
     */
    private abstract class Worker extends Thread {
        /**
         * Variable to check available of instance.
         */
        protected boolean available = true;

        /**
         * The average for normal distribution.
         */
        protected Double mean;

        /**
         * Variable needs to set reference to the {@link Queue} class.
         */
        protected Queue queue;

        /**
         * Sigma value for normal distribution.
         */
        protected Double sigma;

        /**
         * Method inform about thread status.
         * 
         * @return The status of {@link Thread} instance.
         */
        public boolean isAvailable() {
            return available;
        }

        /**
         * Method sets status for thread instance.
         * 
         * @param available
         *            The boolean value to set.
         */
        protected void setAvailable(boolean available) {
            this.available = available;
        }
    }

    /**
     * Random number generator for getting values of normal distribution.
     */
    static Random rand = new Random();

    /**
     * Method counts time delay for threads from normal distribution.
     * 
     * @param mean
     *            The average for normal distribution.
     * @param sigma
     *            The sigma value for normal distribution.
     * @return Time of sleep for thread.
     */
    public static long normalDistribution(Double mean, Double sigma) {
        return (long) Math.abs(1000 * (rand.nextGaussian() * sigma + mean));
    }

    /**
     * This object represents the first consumer.
     */
    private Consumer consumer1;

    /**
     * This object represents the second consumer.
     */
    private Consumer consumer2;

    /**
     * This object represents the producer.
     */
    private Producer producer;

    /**
     * The list contains data from file to processing.
     */
    private LinkedList<Integer> queue = null;

    /**
     * Variable contains the size for queue.
     */
    private Integer size;

    /**
     * The constructor for {@link Prod} class.
     * 
     * @param queue
     *            The data from file to processing.
     * @param size
     *            The size for queue.
     */
    public Prod(LinkedList<Integer> queue, Integer size) {
        this.queue = queue;
        this.size = size;

        ExecuteProdProcess();
    }

    /**
     * This method executes all threads operations for {@link Prod} class.
     */
    private void ExecuteProdProcess() {
        Queue queue = new Queue(getQueue());
        producer = new Producer(queue, 0.1, 0.01);
        consumer1 = new Consumer(queue, 0.15, 0.2, false);
        consumer2 = new Consumer(queue, 0.12, 0.3, true);
        producer.start();
        consumer1.start();
        consumer2.start();
    }

    /**
     * Gets the mean value difference between two consumers.
     * 
     * @return The mean value between two consumers.
     */
    public Double getMeanDifference() {
        while (consumer1.isAvailable() || consumer2.isAvailable()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }
        return consumer2.meanValue() - consumer1.meanValue();
    }

    /**
     * This method return the data from file.
     * 
     * @return the queue
     */
    public LinkedList<Integer> getQueue() {
        return queue;
    }

    /**
     * This method return the size, which was given as argument.
     * 
     * @return the size
     */
    public Integer getSize() {
        return size;
    }

}
