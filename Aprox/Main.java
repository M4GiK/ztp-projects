/**
 * Project Praca domowa 08 – aprox
 * Copyright Michał Szczygieł
 * Created at Dec 18, 2013.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The core of project. Connecting with database, fetching the data and passing
 * value to Aprox algorithm.
 * 
 * @author Michał Szczygieł <michal.szczygiel@wp.pl>
 * 
 */
public class Main {

    /**
     * Collection keeps need format for Date.
     */
    private static List<DateFormat> formats = new LinkedList<DateFormat>(
            buildDateFormats());

    /**
     * Query to be executed on database
     */
    private static final String QUERY = "SELECT y, t FROM Ttable WHERE t BETWEEN ? AND ?";

    /**
     * This method builds list of DateFormats for Collection of DateFormat.
     */
    private static List<DateFormat> buildDateFormats() {
        List<DateFormat> formats = new LinkedList<DateFormat>();

        formats.add(DateFormat.getDateInstance(DateFormat.SHORT,
                Locale.getDefault()));
        formats.add(DateFormat.getDateInstance(DateFormat.MEDIUM,
                Locale.getDefault()));
        formats.add(DateFormat.getDateInstance(DateFormat.LONG,
                Locale.getDefault()));
        formats.add(DateFormat.getDateInstance(DateFormat.FULL,
                Locale.getDefault()));

        return formats;
    }

    /**
     * Method connecting to database and gets the data needed to perform the
     * Aprox algorithm
     * 
     * @param connector
     * @param t0
     *            The datetime calculate from 8:00.
     * @param tk
     *            The end time calculate to 7:59 next day.
     * 
     * @return The Map with dates in List with key which is result of function y
     *         = f(t).
     */
    public static Map<Timestamp, Float> dbconnection(final String connector,
            Timestamp t0, Timestamp tk) {

        Map<Timestamp, Float> data = null;

        try {

            Connection conn = DriverManager.getConnection(connector);
            PreparedStatement statement = conn.prepareStatement(QUERY);
            statement.setTimestamp(1, t0);
            statement.setTimestamp(2, tk);

            data = retriveData(statement.executeQuery());

            statement.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * This method changes executable param of date in string format to object
     * of {@link java.sql.Timestamp}.
     * 
     * @param date
     *            The string with date value;
     * @param milliPerDay
     * @return The Object of {@link java.sql.Timestamp}.
     */
    private static Timestamp getDate(Long milliPerDay, final String... date) {
        String complexDate = "";
        for (int i = 1; i < date.length; i++) {
            complexDate += date[i] + " ";
        }

        for (DateFormat format : formats) {
            try {
                return new Timestamp(format.parse(complexDate).getTime()
                        + 28800000L + milliPerDay);
            } catch (ParseException e) {
                continue;
            }
        }

        return null;
    }

    /**
     * Main method. Executing and solving the problem. Should have only two
     * parameter: -connection_string -date.
     * 
     * @param args
     *            connection_string, date.
     */
    public static void main(final String... args) {
        try {
            Map<Timestamp, Float> data = dbconnection(args[0].trim(),
                    getDate(0L, args), getDate(Aprox.MILLI_PER_DAY, args));
            Aprox aprox = new Aprox(data, getDate(0L, args));

            System.out
                    .format(Locale.US,
                            "Wynik : %.5f",
                            aprox.getApproximationValue((double) Aprox.MINUTES_PER_DAY));
        } catch (Exception ex) {
            System.out.format(Locale.US, "Wynik : %.5f", (double) Aprox.ERROR);
        }
    }

    /**
     * This method gets data from {@link ResultSet} to list contains data and
     * result of function y = f(t).
     * 
     * @param result
     *            The set with fetched data
     * @return The Map with dates in List with key which is t and value is
     *         result of function y = f(t).
     * @throws SQLException
     */
    private static Map<Timestamp, Float> retriveData(final ResultSet result)
            throws SQLException {
        Map<Timestamp, Float> dateCollection = new HashMap<Timestamp, Float>();

        while (result.next()) {
            Float y = result.getFloat("y");
            if (!dateCollection.containsKey(result.getTimestamp("t"))) {
                dateCollection.put(result.getTimestamp("t"), y);
            }
        }

        return dateCollection;
    }

}
