import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Project Praca domowa 02 – hashing.
 * Copyright Michał Szczygieł
 * Created at Oct 24, 2013.
 */

/**
 * Class responsible for reading data from file and putting to Hash class.
 * 
 * @author Michał Szczygieł <michal.szczygiel@wp.pl>
 * 
 */
public final class Hashing {
    /**
     * Inserting LinkedList of strings into hash table.
     * 
     * @param hash
     * 
     * @param list
     * 
     */
    private static void chaining(Hash hash, LinkedList<String> list) {
        for (String string : list) {
            hash.insert(string);
        }
    }

    /**
     * Main method. Executing and solving the problem. Should have only one
     * parameter: -file name. -size of hash table.
     * 
     * @param args
     * 
     * @throws FileNotFoundException
     */
    public static void main(String... args) throws FileNotFoundException {
        if (args.length == 2) {
            LinkedList<String> strings = readHashFile(new File(args[0]));
            Hash hash = new Hash(Integer.parseInt(args[1]));
            chaining(hash, strings);

            System.out.println("Ilość kolizji : " + hash.getCollisionAmount());
        }
    }

    /**
     * Reading the hash table from file.
     * 
     * @param filePath
     * 
     * @return LinkedList of hash table
     * 
     * @throws FileNotFoundException
     */
    private static LinkedList<String> readHashFile(File file)
            throws FileNotFoundException {
        LinkedList<String> hash = new LinkedList<String>();

        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            hash.add(scanner.nextLine());
        }

        scanner.close();

        return hash;
    }
}
