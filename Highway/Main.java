import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Project Praca domowa 03 – highway.
 * Copyright Michał Szczygieł
 * Created at Oct 30, 2013.
 */

/**
 * 
 * Main class, solving problem with possibilities of building new highways.
 * 
 * @author Michał Szczygieł <michal.szczygiel@wp.pl>
 * 
 */
public class Main {

    /**
     * Main method. Executing and solving the problem. Should have only one
     * parameter: -file name.
     * 
     * @param args
     *            file name
     * 
     * @throws FileNotFoundException
     */
    public static void main(final String... args) throws FileNotFoundException {

        if (args.length == 1) {
            Highway highway = new Highway(readFile(new File(args[0])));
            System.out.println("Wynik : " + highway.getStatus());
        }
    }

    /**
     * This method, reads data from file and fills arrays with proper data.
     * 
     * @param file
     *            with data contains locations of cities.
     * @return list of data with locations of cities.
     * @throws FileNotFoundException
     */
    private static ArrayList<Integer> readFile(File file)
            throws FileNotFoundException {

        ArrayList<Integer> data = new ArrayList<Integer>();
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String[] lines = scanner.nextLine().split("[^0-9]+");
            for (String line : lines) {
                if (line.matches("[0-9]+")) {
                    data.add(Integer.parseInt(line));
                }
            }
        }
        scanner.close();

        return data;
    }
}
