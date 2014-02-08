/**
 * Project Praca domowa 06 – plane
 * Copyright Michał Szczygieł
 * Created at Dec 04, 2013.
 */

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * This class represtens a solution of finding the largest region area O, where
 * O is subarea region of area T.
 * 
 * @author Michał Szczygieł <michal.szczygiel@wp.pl>
 * 
 */
public class Plain {

    /**
     * This class represents comparator for ponits in two dimension.
     */
    private class CoordinateComparator implements Comparator<Point2D.Float> {

        /**
         * The constructor for CoordinateComparator. Needs to initalize to avoid
         * Class Cast Exception.
         */
        public CoordinateComparator() {
        }

        /**
         * Compares two points in lexicographical approach. This method
         * overrides an existing method.
         * 
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(Point2D.Float p1, Point2D.Float p2) {
            if (p1.getX() < p2.getX()) {
                return -1;
            }
            if (p1.getX() > p2.getX()) {
                return 1;
            }
            if (p1.getY() < p2.getY()) {
                return -1;
            }
            if (p1.getY() > p2.getY()) {
                return 1;
            }
            return 0;
        }
    }

    /**
     * The map of regions, where value key is result of f(x,y), x and y are part
     * of Piont2D.
     */
    private Map<Float, List<Point2D.Float>> regionsMap = null;

    /**
     * Contructor of the Plan class. Initialize regionsMap from argument.
     * 
     * @param coordinates
     *            Data from Database to solve algorithm.
     */
    public Plain(Map<Float, List<Point2D.Float>> coordinates) {
        this.regionsMap = coordinates;
    }

    /**
     * Builds a convex hull for top or bottom part.
     * 
     * @param points
     *            To create convex hull.
     * @return The part of convex hull.
     */
    private List<Point2D.Float> buildHull(List<Point2D.Float> points) {
        List<Point2D.Float> hull = new ArrayList<Point2D.Float>();

        for (Point2D.Float point : points) {
            while (hull.size() > 1
                    && getCross(hull.get(hull.size() - 2),
                            hull.get(hull.size() - 1), point) <= 0) {
                hull.remove(hull.size() - 1);
            }
            hull.add(point);
        }

        return hull;
    }

    /**
     * Methods create Convex Hull to solve problem. Monotone chain aka Andrew's
     * algorithm — O(n log n) Published in 1979 by A. M. Andrew. The algorithm
     * can be seen as a variant of Graham scan which sorts the points
     * lexicographically by their coordinates. When the input is already sorted,
     * the algorithm takes O(n) time.
     * 
     * @param points
     *            The pionts creates the a convex hull.
     * @return The list of point, which crates a convex.
     */
    private List<Point2D.Float> createConvexHull(List<Point2D.Float> points) {
        Collections.sort(points, new CoordinateComparator());
        List<Point2D.Float> lowerHull = buildHull(points);

        Collections.reverse(points);
        List<Point2D.Float> upperHull = buildHull(points);

        upperHull.addAll(lowerHull);

        return upperHull;
    }

    /**
     * This method calculates the area from the given points.
     * 
     * @param region
     *            The regiong builds from points.
     * @return The area size for given region as parameter.
     */
    private Double getArea(List<Point2D.Float> region) {
        Double area = 0.0;

        List<Point2D.Float> convexHull = createConvexHull(region);
        Point2D.Float firstPoint = convexHull.get(0);

        for (int i = 1; i < convexHull.size() - 1; i++) {
            area += getTrangulizationArea(firstPoint, convexHull.get(i),
                    convexHull.get(i + 1));
        }

        return area;
    }

    /**
     * 2D cross product of OA and OB vectors. Returns a positive value, if OAB
     * makes a counter-clockwise turn, negative for clockwise turn, and zero if
     * the points are collinear.
     * 
     * @param O
     *            The current point.
     * @param A
     *            The penultimate of vector.
     * @param B
     *            The last point of vector.
     * @return The corss product of OA and OB.
     */
    private Double getCross(Point2D O, Point2D A, Point2D B) {
        return (A.getX() - O.getX()) * (B.getY() - O.getY())
                - (A.getY() - O.getY()) * (B.getX() - O.getX());
    }

    /**
     * This method finding the largest region from Map of points, which create a
     * regions.
     * 
     * @return The Largest area from regions.
     */
    public Double getLargestRegionArea() {
        Double maxArea = 0.0;

        for (Entry<Float, List<Point2D.Float>> region : regionsMap.entrySet()) {
            Double currentArea = getArea(region.getValue());

            if (currentArea > maxArea) {
                maxArea = currentArea;
            }
        }

        return maxArea;
    }

    /**
     * This method calculate area for traingle, using Heron formule. Takes three
     * next points which are part of convex, that creates a triangle.
     * 
     * @param firstPoint
     * @param secondPoint
     * @param thirdPoint
     * @return The area of traingle.
     */
    private Double getTrangulizationArea(Point2D.Float firstPoint,
            Point2D.Float secondPoint, Point2D.Float thirdPoint) {
        Double a = firstPoint.distance(secondPoint);
        Double b = secondPoint.distance(thirdPoint);
        Double c = thirdPoint.distance(firstPoint);
        Double s = (a + b + c) / 2.0;

        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }
}
