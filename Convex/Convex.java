import java.util.ArrayList;

/**
 * Project Praca domowa 04 – convex.
 * Copyright Michał Szczygieł
 * Created at Nov 12, 2013.
 */

/**
 * This class creates Convex using hull intremental method.
 * 
 * @author Michał Szczygieł <michal.szczygiel@wp.pl>
 * 
 */
public class Convex {

    /**
     * Implementation face of Convex
     */
    private final class Face {

        private double a;
        private double b;
        private double c;
        private double d;

        /** Index of point in list of all points */
        private int index0;

        /** Index of point in list of all points */
        private int index1;

        /** Index of point in list of all points */
        private int index2;

        /**
         * Constructor for face.
         * 
         * @param index0
         * @param index1
         * @param index2
         */
        public Face(int index0, int index1, int index2) {
            this.index0 = index0;
            this.index1 = index1;
            this.index2 = index2;

            calculatePlane();
        }

        /**
         * @return The area for current face.
         */
        public Float area() {
            Float a = distance(points.get(index0), points.get(index1));
            Float b = distance(points.get(index1), points.get(index2));
            Float c = distance(points.get(index2), points.get(index0));
            Float s = (a + b + c) / 2.0f;

            return (float) Math.sqrt(s * (s - a) * (s - b) * (s - c));
        }

        /**
         * Calculate edges for face.
         */
        private void calculatePlane() {
            Point3D point1 = points.get(this.index0);
            Point3D point2 = points.get(this.index1);
            Point3D point3 = points.get(this.index2);

            this.a = point1.getY() * (point2.getZ() - point3.getZ())
                    + point2.getY() * (point3.getZ() - point1.getZ())
                    + point3.getY() * (point1.getZ() - point2.getZ());
            this.b = point1.getZ() * (point2.getX() - point3.getX())
                    + point2.getZ() * (point3.getX() - point1.getX())
                    + point1.getZ() * (point1.getX() - point2.getX());
            this.c = point1.getX() * (point2.getY() - point3.getY())
                    + point2.getX() * (point3.getY() - point1.getY())
                    + point3.getX() * (point1.getY() - point2.getY());
            this.d = -(point1.getX()
                    * (point2.getY() * point3.getZ() - point3.getY()
                            * point2.getZ())
                    + point2.getX()
                    * (point3.getY() * point1.getZ() - point1.getY()
                            * point3.getZ()) + point3.getX()
                    * (point1.getY() * point2.getZ() - point2.getY()
                            * point1.getZ()));
        }

        /**
         * Calculate distance beetwen points
         * 
         * @param pointA
         * @param pointB
         * @return The distance bettwen two points.
         */
        private Float distance(Point3D pointA, Point3D pointB) {
            return (float) Math.sqrt(Math.pow(pointA.getX() - pointB.getX(), 2)
                    + Math.pow(pointA.getY() - pointB.getY(), 2)
                    + Math.pow(pointA.getZ() - pointB.getZ(), 2));
        }

        /**
         * Method flips the face.
         */
        public void flip() {
            int temp = index0;
            index0 = index1;
            index1 = temp;

            calculatePlane();
        }

        /**
         * In mathematics and physics, the centroid or geometric center of a
         * two-dimensional region is, informally, the point at which a cardboard
         * cut-out of the region could be perfectly balanced on the tip of a
         * pencil (assuming uniform density and a uniform gravitational field).
         * 
         * @return The point which is centroid for current face.
         */
        public Point3D getCentroid() {
            Point3D point0 = points.get(index0);
            Point3D point1 = points.get(index1);
            Point3D point2 = points.get(index2);

            return new Convex().new Point3D(
                    (point0.getX() + point1.getX() + point2.getX()) / 3,
                    (point0.getY() + point1.getY() + point2.getY()) / 3,
                    (point0.getZ() + point1.getZ() + point2.getZ()) / 3);

        }

        /**
         * Method checks if face is visible from outside convex.
         * 
         * @param point
         *            To checks correct side.
         * @return true if plane is plane visible from proper side, false if is
         *         not.
         */
        public boolean isVisible(Point3D point) {
            return (a * point.getX() + b * point.getY() + c * point.getZ() + d) > 0;
        }
    }

    /**
     * Representation of an object in 3D space.
     */
    public class Point3D {

        /** Coordinate representing the x-axis */
        private float x;

        /** Coordinate representing the y-axis */
        private float y;

        /** Coordinate representing the z-axis */
        private float z;

        /**
         * Constructor. Sets coordinates for instance.
         * 
         * @param x
         *            The coordinate representing the x-axis.
         * @param y
         *            The coordinate representing the y-axis.
         * @param z
         *            The coordinate representing the z-axis.
         */
        public Point3D(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        /**
         * @return the x
         */
        public float getX() {
            return x;
        }

        /**
         * @return the y
         */
        public float getY() {
            return y;
        }

        /**
         * @return the z
         */
        public float getZ() {
            return z;
        }
    }

    /** The list contains the coordinates for Convex */
    private static ArrayList<Point3D> points = new ArrayList<Point3D>();

    /** The list contains the faces belong to Convex */
    private ArrayList<Face> validFaces = null;

    /**
     * Empty constructor.
     */
    public Convex() {
    }

    /**
     * Constructor, creates Convex using hull intremental method.
     * 
     * @param coordinates
     */
    public Convex(ArrayList<Point3D> coordinates) {

        // Creates copy of points
        Convex.points = coordinates;
        createConvex();

    }

    /**
     * Add next point to the convex.
     * 
     * @param point3d
     * @param index
     */
    private void addNextPoint(Point3D point3d, int index) {
        ArrayList<Face> visableFaces = new ArrayList<Face>();

        for (Face face : validFaces) {
            if (face.isVisible(point3d)) {
                visableFaces.add(face);
            }
        }

        if (visableFaces.size() != 0) {

            for (Face face : visableFaces) {
                validFaces.remove(validFaces.indexOf(face));
            }

            if (visableFaces.size() == 1) {
                Face face = visableFaces.get(0);
                validFaces.add(new Face(index, face.index0, face.index1));
                validFaces.add(new Face(index, face.index1, face.index2));
                validFaces.add(new Face(index, face.index2, face.index0));
            } else {
                validFaces.addAll(findNextFace(index, visableFaces));
            }
        }
    }

    /**
     * The surface area of a tetrahedron/** The surface area of a tetrahedron is
     * the sum of the areas of the triangular faces. Heron's formula gives the
     * area of a triangle when you input the lengths of the sides. Given
     * triangular edge lengths of x, y, and z, the area function H(x, y, z) is
     * defined by the expression
     * 
     * H(x,y,z) = 0.25√(2x²y² + 2x²z² + 2y²z² - x^4 - y^4 - z^4)
     * 
     * Therefore, the surface area of the entire tetrahedron is
     * 
     * H(a, b, c) + H(a, e, f) + H(b, d, f) + H(c, d, e)
     */
    public Float camputeArea() {
        Float area = 0.0f;

        for (Face face : validFaces) {
            area += face.area();
        }

        return area;
    }

    /**
     * Finds the center of tetrhedron.
     * 
     * @param points
     * @param index
     * @param face
     * @return The point be a center of tetrahedron.
     */
    private Point3D centerOfTetrahedron(ArrayList<Point3D> points, int index,
            Face face) {
        Point3D point = points.get(index);
        Point3D point0 = points.get(face.index0);
        Point3D point1 = points.get(face.index1);
        Point3D point2 = points.get(face.index2);

        return new Point3D(
                (point.getX() + point0.getX() + point1.getX() + point2.getX()) / 4,
                (point.getY() + point0.getY() + point1.getY() + point2.getY()) / 4,
                (point.getZ() + point0.getZ() + point1.getZ() + point2.getZ()) / 4);
    }

    /**
     * This method creates Convex using hull intremental method.
     */
    private void createConvex() {

        validFaces = createTetrahedron();

        for (int i = 4; i < points.size(); i++) {
            addNextPoint(points.get(i), i);
        }
    }

    /**
     * Creates tetrahedron using first 4 points.
     * 
     * @return The faces creates tetrahedron.
     */
    private ArrayList<Face> createTetrahedron() {
        ArrayList<Face> tetrahedronFaces = new ArrayList<Face>();

        Face initialFace = new Face(0, 1, 2);
        tetrahedronFaces.add(initialFace);
        tetrahedronFaces
                .add(new Face(3, initialFace.index0, initialFace.index1));
        tetrahedronFaces
                .add(new Face(3, initialFace.index1, initialFace.index2));
        tetrahedronFaces
                .add(new Face(3, initialFace.index2, initialFace.index0));

        Point3D center = centerOfTetrahedron(points, 3, initialFace);

        for (Face face : tetrahedronFaces) {
            if (face.isVisible(center)) {
                face.flip();
            }
        }

        return tetrahedronFaces;

    }

    /**
     * Method finds next faces of convex.
     * 
     * @param index
     * @param visableFaces
     * @return The faces of convex.
     */
    private ArrayList<Face> findNextFace(int index, ArrayList<Face> visableFaces) {
        ArrayList<Face> tempFaces = new ArrayList<Face>();
        for (Face face : visableFaces) {
            tempFaces.add(new Face(index, face.index0, face.index1));
            tempFaces.add(new Face(index, face.index1, face.index2));
            tempFaces.add(new Face(index, face.index2, face.index0));
        }

        return theMostDistatnFaces(tempFaces, index);
    }

    /**
     * Method finds the most distatnt faces of convex.
     * 
     * @param tempFaces
     * @param index
     * @return
     */
    private ArrayList<Face> theMostDistatnFaces(ArrayList<Face> tempFaces,
            int index) {
        ArrayList<Face> distantFaces = new ArrayList<Face>();

        for (Face face : tempFaces) {
            boolean isOuter = true;
            for (Face other : tempFaces) {
                if (face.isVisible(other.getCentroid())) {
                    isOuter = false;
                }
            }

            if (isOuter) {
                distantFaces.add(face);
            }
        }
        return distantFaces;
    }
}
