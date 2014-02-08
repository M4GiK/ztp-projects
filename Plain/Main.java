/**
 * Project Praca domowa 06 – plane
 * Copyright Michał Szczygieł
 * Created at Dec 04, 2013.
 */

import java.awt.geom.Point2D;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The core of project. Connecting with database, fetching the data and passing
 * value to Plain algorithm.
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
     * Plain algorithm
     * 
     * @param connector
     * @return The Map with coordinates in 2D space with key which is result of
     *         function z = f(x,y).
     */
    public static Map<Float, List<Point2D.Float>> dbconnection(
            final String connector) {

        Map<Float, List<Point2D.Float>> coordinates = null;

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
            Map<Float, List<Point2D.Float>> coordinates = dbconnection(args[0]
                    .trim());
            Plain plain = new Plain(coordinates);
            System.out.format(Locale.US, "Maksimum : %.5f",
                    plain.getLargestRegionArea());
        }
    }

    /**
     * This method gets data from {@link ResultSet} to list contains points in
     * 2D space and result of function z = f(x,y).
     * 
     * @param result
     *            The set with fetch data
     * @return The HasjMap with coordinates in 2D space with key which is result
     *         of function z = f(x,y).
     * @throws SQLException
     */
    private static Map<Float, List<Point2D.Float>> retriveData(
            final ResultSet result) throws SQLException {
        Map<Float, List<Point2D.Float>> coordinates = new HashMap<Float, List<Point2D.Float>>();

        while (result.next()) {
            Point2D.Float point = new Point2D.Float(result.getFloat("x"),
                    result.getFloat("y"));

            if (coordinates.containsKey(result.getFloat("z"))) {
                coordinates.get(result.getFloat("z")).add(point);
            } else {
                List<Point2D.Float> points = new LinkedList<Point2D.Float>();
                points.add(point);
                coordinates.put(result.getFloat("z"), points);
            }
        }

        return coordinates;
    }
}
