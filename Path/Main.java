/**
 * Project Praca domowa 010 – path
 * Copyright Michał Szczygieł
 * Created at Jan 17, 2014.
 */

import java.awt.geom.Point2D;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The core of project. Connecting with database, fetching the data and passing
 * value to Path algorithm based on Dinic algorithm in O(V^2*E).
 * 
 * @author Michał Szczygieł <michal.szczygiel@wp.pl>
 * 
 */
public class Main {

    /**
     * Query to be executed on database
     */
    private static final String QUERY = "SELECT x, y, p FROM Gtable";

    /**
     * Method connecting to database and gets the data needed to perform the
     * Plain algorithm
     * 
     * @param connector
     * @return The Map with coordinates in 2D space with key and capacity as
     *         value.
     */
    public static Map<Point2D, Float> dbconnection(final String connector) {

        Map<Point2D, Float> coordinates = null;

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
     * Main method. Executing and solving the problem. Should have only two
     * parameter: -connection_string -date.
     * 
     * @param args
     *            connection_string, date.
     * @throws IOException
     */
    public static void main(final String... args) throws IOException {
        try {
            System.out.format(Locale.US, "Przepustowosc : %.3f", new Path(
                    dbconnection(args[0].trim()))
                    .optimalTransportationCost(Integer.parseInt(args[1])));
        } catch (Exception ex) {
            System.out.format(Locale.US, "Przepustowosc : %.3f", 0f);
        }
    }

    /**
     * This method gets data from {@link ResultSet} to list contains points in
     * 2D space and capacity.
     * 
     * @param result
     *            The set with fetch data
     * @return The HashMap with coordinates in 2D space and capacity as value.
     * @throws SQLException
     */
    private static Map<Point2D, Float> retriveData(final ResultSet result)
            throws SQLException {
        Map<Point2D, Float> coordinates = new HashMap<Point2D, Float>();

        while (result.next()) {
            Point2D point = new Point2D.Float(result.getInt("x"),
                    result.getInt("y"));
            coordinates.put(point, result.getFloat("p"));
        }

        return coordinates;
    }
}
