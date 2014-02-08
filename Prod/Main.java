/**
 * Project Praca domowa 05 – prod.
 * Copyright Michał Szczygieł
 * Created at Nov 21, 2013.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Scanner;

/**
 * The core of project. This class gets the data from file and pass values to
 * Prod algorithm.
 * 
 * @author Michał Szczygieł <michal.szczygiel@wp.pl>
 * 
 */
public class Main {

    /**
     * Main method. Executing and solving the problem. Should have only two
     * parameters: -file path. -size of queue list.
     * 
     * @param args
     * 
     * @throws FileNotFoundException
     */
    public static void main(final String... args) throws FileNotFoundException {
        if (args.length == 2) {
            if (Integer.parseInt(args[1]) > 0) {
                LinkedList<Integer> integersList = readFile(new File(args[0]));
                Prod prod = new Prod(integersList, Integer.parseInt(args[1]));
                System.out.format(Locale.US, "Wynik : %.3f ",
                        prod.getMeanDifference());
            }
        }
    }

    /**
     * Reading the data contains integers from file.
     * 
     * @param filePath
     *            The file to read data.
     * @param size
     *            Size of queue.
     * 
     * @return LinkedList of integers.
     * 
     * @throws FileNotFoundException
     */
    private static LinkedList<Integer> readFile(File file)
            throws FileNotFoundException {
        LinkedList<Integer> integersList = new LinkedList<Integer>();

        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String[] lines = scanner.nextLine().split("[^0-9-]+");
            for (String line : lines) {
                if (line.matches("[-]{0,1}[0-9]+")) {
                    integersList.add(Integer.parseInt(line));
                }
            }
        }

        scanner.close();

        return integersList;
    }

}
