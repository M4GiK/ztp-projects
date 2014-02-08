/**
 * Praca domowa 07 – classification
 * Copyright Michał Szczygieł
 * Created at Dec 11, 2013.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class represent simple process of learning and recognizing data.
 * Algorithm used method to finding the nearest neighbor for normalized
 * vectors*.
 * 
 * *The normalized is to be understood as the average value for all vectors v_i.
 * 
 * @author Michał Szczygieł <michal.szczygiel@wp.pl>
 * 
 */
public class Classification {

    /**
     * Vector Collection contains normalized distribution of vectors for the
     * relevant classes.
     */
    private Map<Integer, ArrayList<Double>> vectorCollectionNormalized;

    /**
     * Vector Collection contain data to "learn" application.
     */
    private Map<Integer, ArrayList<ArrayList<Double>>> vectorCollectionToLearn;

    /**
     * Vector Collection contain data to recognize.
     */
    private Map<Integer, ArrayList<ArrayList<Double>>> vectorCollectionToRecognize;

    /**
     * The constructor for {@link Classification} class.
     * 
     * @param vectorCollection
     *            The data from file to processing.
     * 
     */
    public Classification(ArrayList<Double> dataFromFile, Integer size) {
        createVectorsCollection(dataFromFile, size);
        learn(this.vectorCollectionToLearn);
    }

    /**
     * Calculate nearest neighbor for each vector, and comparison vectors to
     * which class belong.
     * 
     * @return The String result, which contain the recognized class for
     *         vectors.
     */
    public String classified() {
        String result = "";

        for (Entry<Integer, ArrayList<ArrayList<Double>>> entry : vectorCollectionToRecognize
                .entrySet()) {
            for (ArrayList<Double> vector : entry.getValue()) {
                result += findCorrelation(vector);
            }
        }

        return result;
    }

    /**
     * This method creates proper vector, to learn or to recognize.
     * 
     * @param vectorCollection
     *            The vector to be initialize.
     * @param dataFromFile
     *            The data from file to fill the vector.
     * @param current
     *            The current vector from file.
     * @param size
     *            The size of vector.
     */
    private void createVector(
            Map<Integer, ArrayList<ArrayList<Double>>> vectorCollection,
            ArrayList<Double> dataFromFile, Integer current, Integer size) {

        Integer vectorClass = dataFromFile.get(current).intValue();
        ArrayList<Double> vector = new ArrayList<Double>();

        for (int j = current + 1; j < current + size + 1; j++) {
            vector.add(dataFromFile.get(j));
        }

        if (!vectorCollection.containsKey(vectorClass)) {
            vectorCollection.put(vectorClass,
                    new ArrayList<ArrayList<Double>>());
        }

        vectorCollection.get(vectorClass).add(vector);
    }

    /**
     * This method creates from input file, collections of read-in vectors.
     * 
     * @param dataFromFile
     *            The data get from file.
     * @param size
     *            The size for vector.
     * @return
     */
    private void createVectorsCollection(ArrayList<Double> dataFromFile,
            Integer size) {
        vectorCollectionNormalized = new HashMap<Integer, ArrayList<Double>>();
        vectorCollectionToLearn = new HashMap<Integer, ArrayList<ArrayList<Double>>>();
        vectorCollectionToRecognize = new HashMap<Integer, ArrayList<ArrayList<Double>>>();
        Boolean isZero = false;

        for (int i = 0; i < dataFromFile.size(); i += size + 1) {

            if (dataFromFile.get(i).intValue() == 0) {
                isZero = true;
            }

            if (!isZero) {
                createVector(this.vectorCollectionToLearn, dataFromFile, i,
                        size);
            } else {
                createVector(this.vectorCollectionToRecognize, dataFromFile, i,
                        size);
            }
        }
    }

    /**
     * This method finds, the proper class for given vector.
     * 
     * @param vector
     *            The vector to recognize.
     * @return String with proper class.
     */
    private String findCorrelation(ArrayList<Double> vector) {
        String resultClass = null;
        Double minLength = Double.MAX_VALUE;
        Double vecLenght = 0.0;

        for (Integer key : vectorCollectionNormalized.keySet()) {
            ArrayList<Double> subVector = vectorCollectionNormalized.get(key);
            for (int index = 0; index < subVector.size(); ++index) {
                vecLenght += (Math.pow(
                        vector.get(index) - subVector.get(index), 2.0));
            }

            vecLenght = Math.sqrt(vecLenght);

            if (minLength > vecLenght) {
                minLength = vecLenght;
                resultClass = key.toString();
            }
        }

        return resultClass;
    }

    /**
     * Prepare the calculation for nearest neighbor in the normalized vector
     * collection. For each input class, calculates the average vector, and this
     * average vector will be later used to search for nearest neighbor of input
     * vectors to be classified.
     * 
     * @param vectorCollection
     *            The vector to learn.
     */
    private void learn(
            Map<Integer, ArrayList<ArrayList<Double>>> vectorCollection) {
        for (Integer key : vectorCollection.keySet()) {
            ArrayList<Double> neighbor = prepareArrayList(vectorCollection
                    .get(key).get(0).size());

            for (ArrayList<Double> vector : vectorCollection.get(key)) {
                for (int index = 0; index < vector.size(); index++) {
                    neighbor.set(index, neighbor.get(index) + vector.get(index));
                }
            }

            for (int index = 0; index < neighbor.size(); index++) {
                neighbor.set(index,
                        neighbor.get(index) / vectorCollection.get(key).size());
            }

            this.vectorCollectionNormalized.put(key, neighbor);
        }
    }

    /**
     * This method using given size, prepare an empty ArrayList of Doubles.
     * 
     * @param size
     *            The size to create empty ArrayList
     * @return Empty ArrayList of Doubles.
     */
    private ArrayList<Double> prepareArrayList(Integer size) {
        ArrayList<Double> result = new ArrayList<Double>();

        for (int index = 0; index < size; index++) {
            result.add(new Double(0.0));
        }

        return result;
    }

}
