/**
 * Project Praca domowa 01 – plate.
 * Copyright Michał Szczygieł
 * Created at Oct 16, 2013.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main class.
 * 
 * @author Michał Szczygieł <michal.szczygiel@wp.pl>
 * 
 */
public class Main {

    /**
     * Main method. Executing and solving the problem. Should have only one parameter: -file name.
     * 
     * @param args
     * @throws FileNotFoundException
     */
    public static void main(String... args) throws FileNotFoundException {

        if (args.length == 1) {
            Main main = new Main();

            if (main.readFile(new File(args[0]))) {
                Plate plate = new Plate(main.getArrayX(), main.getArrayY());
                System.out.println(plate.cutCost());
            }
        }

    }

    /**
     * Variable stores data with weights for horizontal cuts.
     */
    private ArrayList<Integer> arrayX = null;

    /**
     * Variable stores data with weights for vertical cuts.
     */
    private ArrayList<Integer> arrayY = null;

    /**
     * Method fills data from file to proper arrays. First and Second element are a dimension (nxm). Another values are:
     * x1, x2, . . . , xm-1; y1, y2, . . . , yn-1;
     * 
     * @param data
     * @return
     */
    private boolean fillData(ArrayList<String> data) {
        boolean isFilled = false;

        if (Integer.parseInt(data.get(1)) + Integer.parseInt(data.get(0)) - 2 <= data
            .size()
            && Integer.parseInt(data.get(1)) + Integer.parseInt(data.get(0))
                - 2 >= 0) {

            ArrayList<Integer> arrayX = new ArrayList<Integer>(
                Integer.parseInt(data.get(1)) - 1);

            for (int i = 2; i < Integer.parseInt(data.get(1)) + 1; i++) {
                arrayX.add(Integer.parseInt(data.get(i)));
            }

            setArrayX(arrayX);

            ArrayList<Integer> arrayY = new ArrayList<Integer>(
                Integer.parseInt(data.get(0)) - 1);

            for (int i = Integer.parseInt(data.get(1)) + 1; i < (Integer
                .parseInt(data.get(0)) - 1)
                + (Integer.parseInt(data.get(1)) + 1); i++) {
                arrayY.add(Integer.parseInt(data.get(i)));
            }

            setArrayY(arrayY);

            isFilled = true;

        }

        return isFilled;
    }

    /**
     * @return the arrayX
     */
    public ArrayList<Integer> getArrayX() {
        return arrayX;
    }

    /**
     * @return the arrayY
     */
    public ArrayList<Integer> getArrayY() {
        return arrayY;
    }

    /**
     * This method, read data from file and check correctness of the data. Also fill arrays with proper data.
     * 
     * @param fileName
     * @return true if read correctly or false if not.
     * @throws FileNotFoundException
     */
    private boolean readFile(File fileName) throws FileNotFoundException {
        boolean isReaded = false;

        ArrayList<String> data = new ArrayList<String>();

        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(fileName);

        while (scanner.hasNextLine()) {
            String[] lines = scanner.nextLine().split("[^0-9]+");
            for (String line : lines) {
                if (line.matches("[0-9]+")) {
                    data.add(line);
                }
            }
        }

        if (data.size() >= 2 && fillData(data)) {
            isReaded = true;
        }

        return isReaded;
    }

    /**
     * @param arrayX
     *            the arrayX to set
     */
    public void setArrayX(ArrayList<Integer> arrayX) {
        this.arrayX = arrayX;
    }

    /**
     * @param arrayY
     *            the arrayY to set
     */
    public void setArrayY(ArrayList<Integer> arrayY) {
        this.arrayY = arrayY;
    }
}
