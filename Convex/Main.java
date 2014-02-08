/**
 * Project Praca domowa 04 – convex.
 * Copyright Michał Szczygieł
 * Created at Nov 12, 2013.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * The core of project. Connecting with database, fetching the data and passing
 * value to Convex algorithm.
 * 
 * @author Michał Szczygieł <michal.szczygiel@wp.pl>
 * 
 */
public class Main {

    /**
     * Query to be executed on database
     */
    private static final String QUERY = "SELECT x, y, z FROM Ftable";

    /**
     * Method connecting to database and gets the data needed to perform the
     * Convex algorithm
     * 
     * @param connector
     * @return the array with coordinates in 3D space.
     */
    public static ArrayList<Convex.Point3D> dbconnection(final String connector) {

        ArrayList<Convex.Point3D> coordinates = new ArrayList<Convex.Point3D>();

        try {

            Connection conn = DriverManager.getConnection(connector);
            Statement statement = conn.createStatement();

            coordinates = retriveData(statement.executeQuery(QUERY));

            statement.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return coordinates;
    }

    /**
     * Main method. Executing and solving the problem. Should have only one
     * parameter: -connection_string .
     * 
     * @param args
     *            connection_string
     */
    public static void main(final String... args) {

        if (args.length == 1) {

            ArrayList<Convex.Point3D> coordinates = dbconnection(args[0].trim());

            if (coordinates.size() > 3) {

                Convex convexHullIncremental = new Convex(coordinates);
                System.out.println("Powierzchnia : ");
                System.out.format("%.5f ", convexHullIncremental.camputeArea());

            }
        }
    }

    /**
     * This method gets data from {@link ResultSet} to list contains points in
     * 3D space.
     * 
     * @param result
     *            The set with fetch data
     * @return the array with coordinates in 3D space.
     * @throws SQLException
     */
    private static ArrayList<Convex.Point3D> retriveData(ResultSet result)
            throws SQLException {
        ArrayList<Convex.Point3D> coordinates = new ArrayList<Convex.Point3D>();

        while (result.next()) {
            Convex.Point3D point = new Convex().new Point3D(
                    result.getFloat("x"), result.getFloat("y"),
                    result.getFloat("z"));
            coordinates.add(point);

        }

        return coordinates;
    }
}
