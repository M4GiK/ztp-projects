/**
 * Project Praca domowa 01 – plate.
 * Copyright Michał Szczygieł
 * Created at Oct 16, 2013.
 */

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class, counting cost for cutting plate.
 * 
 * @author Michał Szczygieł <michal.szczygiel@wp.pl>
 * 
 */

public class Plate {

    /**
     * Variable stores data with weights for horizontal cuts.
     */
    private ArrayList<Integer> arrayX = null;

    /**
     * Variable stores data with weights for vertical cuts.
     */
    private ArrayList<Integer> arrayY = null;

    /**
     * Constructor for Plate class. Automatically set arrays with values for vertical and horizontal cuts.
     * 
     * @param arrayX
     * @param arrayY
     */
    public Plate(ArrayList<Integer> arrayX, ArrayList<Integer> arrayY) {
        setArrayX(arrayX);
        setArrayY(arrayY);
    }

    /**
     * Method counts cost of cuts.
     * 
     * @return cost of cuts.
     */
    public String cutCost() {

        // Arrays needs sort, to finding optimal cost.
        Collections.sort(getArrayX());
        Collections.sort(getArrayY());

        int accumulator = 0;
        int horizontalLines = 1;
        int verticalLines = 1;
        int sizeX = getArrayX().size();
        int sizeY = getArrayY().size();
        int numberOfIterations = sizeX + sizeY;

        for (int i = 0; i < numberOfIterations; i++) {

            int maxX = 0;
            int maxY = 0;

            if (getArrayX().size() > 0) {
                maxX = Collections.max(getArrayX());
            }

            if (getArrayY().size() > 0) {
                maxY = Collections.max(getArrayY());
            }

            if (maxX > maxY) {
                if (getArrayX().size() > 0) {
                    accumulator += maxX * horizontalLines;
                    getArrayX().remove(sizeX - 1);
                    verticalLines++;
                    sizeX--;
                }

            } else {
                if (getArrayY().size() > 0) {
                    accumulator += maxY * verticalLines;
                    getArrayY().remove(sizeY - 1);
                    horizontalLines++;
                    sizeY--;
                }
            }

        }

        return "Koszt cięcia : " + accumulator;
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
