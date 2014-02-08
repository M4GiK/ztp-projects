/**
 * Praca domowa 09 – hypercube
 * Copyright Michał Szczygieł
 * Created at Jan 8, 2014.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 * The core of project. This class gets the data from file and passes values to
 * class which perform algorithm calculation for hypercube.
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
     * @throws Exception
     */
    public static void main(final String... args) throws Exception {
        if (args.length == 2) {
            if (Integer.parseInt(args[1]) > 0) {
                ArrayList<Double> dataFromFile = readFile(new File(args[0]));
                Algorithm hypercube = new Algorithm(dataFromFile,
                        Integer.parseInt(args[1]));
                try {
                    System.out
                            .format(Locale.US, "Suma : %.5f", hypercube.sum());
                } catch (Exception ex) {
                    System.out.format(Locale.US, "Suma : %.5f\n",
                            Algorithm.sum(dataFromFile));
                }
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
