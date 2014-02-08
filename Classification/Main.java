/**
 * Praca domowa 07 – classification
 * Copyright Michał Szczygieł
 * Created at Dec 11, 2013.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The core of project. This class gets the data from file and pass values to
 * classification algorithm.
 * 
 * @author Michał Szczygieł <michal.szczygiel@wp.pl>
 * 
 */
public class Main {

    /**
     * Main method. Executing and solving the problem. Should have only two
     * parameters: -file path. -size of vector.
     * 
     * @param args
     * 
     * @throws FileNotFoundException
     */
    public static void main(final String... args) throws FileNotFoundException {
        if (args.length == 2) {
            if (Integer.parseInt(args[1]) > 0) {
                ArrayList<Double> dataFromFile = readFile(new File(args[0]));
                Classification classification = new Classification(
                        dataFromFile, Integer.parseInt(args[1]));
                System.out.println("Wyniki klasyfikacji : "
                        + classification.classified());
            }
        }
    }

    /**
     * Reading the data contains data from file.
     * 
     * @param filePath
     *            The file to read data.
     * @param size
     *            Size of vector.
     * 
     * @return ArrayList of data.
     * 
     * @throws FileNotFoundException
     */
    private static ArrayList<Double> readFile(File file)
            throws FileNotFoundException {
        ArrayList<Double> dataList = new ArrayList<Double>();

        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String[] lines = scanner.nextLine().split("[^0-9\\.-]+");
            for (String line : lines) {
                if (line.matches("[-]{0,1}[0-9\\.]+")) {
                    dataList.add(Double.parseDouble(line));
                }

            }
        }

        scanner.close();

        return dataList;
    }

}
