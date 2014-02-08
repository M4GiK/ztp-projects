/**
 * Project Praca domowa 08 – aprox
 * Copyright Michał Szczygieł
 * Created at Dec 18, 2013.
 */

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class represents a solution for problem with cubic aproximiation. This
 * class performs a polynomial (third-degree) approximation of the cubic. Uses
 * the Cholesky' algorithm to finding parameter a for Xa=Y.
 * 
 * @author Michał Szczygieł <michal.szczygiel@wp.pl>
 * 
 */
public class Aprox {

    /**
     * Number of error, to recognize failure approximation.
     */
    public static final Integer ERROR = -1;

    /**
     * Constant value represents day in milliseconds for Aprox problem.
     */
    public static final Long MILLI_PER_DAY = 1439 * 60000L;

    /**
     * Number of minutes in day for Aprox problem.
     */
    public static final Integer MINUTES_PER_DAY = 1439;

    /**
     * Number of dimensions for the equation system.
     */
    private static final Integer N = 4;

    /**
     * Value for computing square for of a BigDecimal using method Newton
     * Raphson.
     */
    private static final BigDecimal SQRT_DIG = new BigDecimal(150);

    /**
     * Value for computing square for of a BigDecimal using method Newton
     * Raphson.
     */
    private static final BigDecimal SQRT_PRE = new BigDecimal(10).pow(SQRT_DIG
            .intValue());

    /**
     * Uses Newton Raphson to compute the square root of a BigDecimal.
     * 
     * @author Luciano Culacciatti
     * @url http://www.codeproject.com/Tips/257031/Implementing-SqrtRoot-in-
     *      BigDecimal
     */
    public static BigDecimal sqrt(BigDecimal c) {
        return sqrtNewtonRaphson(c, new BigDecimal(1),
                new BigDecimal(1).divide(SQRT_PRE));
    }

    /**
     * Private utility method used to compute the square root of a BigDecimal.
     * 
     * @author Luciano Culacciatti
     * @url http://www.codeproject.com/Tips/257031/Implementing-SqrtRoot-in-
     *      BigDecimal
     */
    private static BigDecimal sqrtNewtonRaphson(BigDecimal c, BigDecimal xn,
            BigDecimal precision) {
        BigDecimal fx = xn.pow(2).add(c.negate());
        BigDecimal fpx = xn.multiply(new BigDecimal(2));
        BigDecimal xn1 = fx.divide(fpx, 2 * SQRT_DIG.intValue(),
                RoundingMode.HALF_DOWN);
        xn1 = xn.add(xn1.negate());
        BigDecimal currentSquare = xn1.pow(2);
        BigDecimal currentPrecision = currentSquare.subtract(c);
        currentPrecision = currentPrecision.abs();
        if (currentPrecision.compareTo(precision) <= -1) {
            return xn1;
        }

        return sqrtNewtonRaphson(c, xn1, precision);
    }

    /**
     * Map represents pairs of arguments and values representing the function.
     */
    Map<Timestamp, Float> dateCollection = null;

    /**
     * Matrix triangular lower. Result of Cholesky' decomposition.
     */
    private ArrayList<ArrayList<BigDecimal>> matrixL = null;

    /**
     * Matrix transpose of matrix triangular lower.
     */
    private ArrayList<ArrayList<BigDecimal>> matrixLTranspose = null;

    /**
     * Symmetric matrix, result of build matrix as function args.
     */
    private ArrayList<ArrayList<BigInteger>> symmetricMatrix = null;

    /**
     * The initial time, counted from 8:00 am.
     */
    private Timestamp t0 = null;

    /**
     * Vector acting as 'a' in the X * a = Y equation.
     */
    private ArrayList<BigDecimal> vectorA = null;

    /**
     * Vector acting as 'Y' in the X * a = Y equation.
     */
    private ArrayList<BigDecimal> vectorY = null;

    /**
     * Constructor for {@link Aprox}. Initializes a new instance of the Aprox
     * class using the map of timestamps and values representing the function to
     * be approximated.
     * 
     * @param data
     *            The Timestamp-value pairs representing the function.
     * @param t0
     *            The reference date for deducing function arguments.
     */
    public Aprox(Map<Timestamp, Float> data, Timestamp t0) {
        this.dateCollection = data;
        this.t0 = t0;
        buildSymmetricalMatrix(data);
        buildVectorY(data);
        choleskyDecomposition(symmetricMatrix);
        decompositionLU(matrixL, vectorY);
    }

    /**
     * Calculate the value of this polynomial (third-degree) approximation of
     * the cubic on the argument given as a parameter.
     * 
     * @return Value of the function.
     */
    private BigDecimal approximation(Double x) {
        return (vectorA.get(3).multiply(new BigDecimal(Math.pow(x, 3))))
                .add(vectorA.get(2).multiply(new BigDecimal(Math.pow(x, 2))))
                .add(vectorA.get(1).multiply(new BigDecimal(x)))
                .add(vectorA.get(0));
    }

    /**
     * Performs backward propagation to calculate vector x. Essentially solves
     * the L^T * X = Z equation.
     * 
     * @param matrixLTranspose
     *            Transposed lower triangular (upper triangular).
     * @param z
     *            Vector z, calculated in the forward propagation step.
     * @return Vector x, the solution to the linear equation system.
     */
    private ArrayList<BigDecimal> backwardPropagation(
            ArrayList<ArrayList<BigDecimal>> matrixLTranspose,
            ArrayList<BigDecimal> z) {
        ArrayList<BigDecimal> x = initEmptyArray(z.size());

        for (int i = N - 1; i >= 0; i--) {
            BigDecimal sum = BigDecimal.ZERO;
            for (int j = i + 1; j < N; j++) {
                sum = sum
                        .add(matrixLTranspose.get(i).get(j).multiply(x.get(j)));
            }
            x.set(i, (z.get(i).subtract(sum)).divide(matrixLTranspose.get(i)
                    .get(i), 50, RoundingMode.HALF_UP));
        }

        return x;
    }

    /**
     * Builds symmetrical matrix (4x4) from values of polynomial third degree.
     * 
     * @param data
     *            The collection represents pairs of arguments and values
     *            representing the function.
     */
    private void buildSymmetricalMatrix(Map<Timestamp, Float> data) {
        this.symmetricMatrix = new ArrayList<ArrayList<BigInteger>>();

        for (int j = 0; j < N; j++) {
            ArrayList<BigInteger> matrixCol = new ArrayList<BigInteger>();
            for (int i = 0; i < N; i++) {
                matrixCol.add(sum(i, j));
            }
            symmetricMatrix.add(matrixCol);
        }
    }

    /**
     * Creates the vector Y.
     * 
     * @param data
     *            The collection represents pairs of arguments and values
     *            representing the function.
     */
    private void buildVectorY(Map<Timestamp, Float> data) {
        this.vectorY = new ArrayList<BigDecimal>();

        for (int i = 0; i < N; i++) {
            BigDecimal sum = BigDecimal.ZERO;
            for (Entry<Timestamp, Float> y : dateCollection.entrySet()) {
                sum = sum.add(new BigDecimal(getX(y.getKey()).pow(i))
                        .multiply(new BigDecimal(y.getValue())));
            }
            vectorY.add(sum);
        }
    }

    /**
     * Decomposes the A matrix into lower triangular matrix.
     * 
     * @param a
     *            The symmetrical matrix to decompose.
     * @return
     */
    public void choleskyDecomposition(ArrayList<ArrayList<BigInteger>> a) {
        Integer m = a.size();
        this.matrixL = prepareArrayList(m);

        for (int i = 0; i < m; i++) {
            for (int k = 0; k < (i + 1); k++) {
                BigDecimal sum = BigDecimal.ZERO;
                for (int j = 0; j < k; j++) {
                    sum = sum.add(matrixL.get(i).get(j)
                            .multiply(matrixL.get(k).get(j)));
                }

                if (i == k) {
                    matrixL.get(i).set(
                            k,
                            sqrt(new BigDecimal(a.get(i).get(i)).subtract(sum
                                    .add(sum))));
                } else {
                    matrixL.get(i).set(
                            k,
                            BigDecimal.ONE.divide(
                                    matrixL.get(k)
                                            .get(k)
                                            .multiply(
                                                    new BigDecimal(a.get(i)
                                                            .get(k))
                                                            .subtract(sum)),
                                    50, RoundingMode.HALF_UP));
                }
            }
        }
    }

    /**
     * Solves the linear equation system using LU decomposition and
     * backwards-forward propagation of matrix values to calculate z vector and,
     * finally, the x vector.
     * 
     * @param matrixL
     *            The lower triangular matrix, result value of cholesky
     *            decomposition.
     * @param vectorY
     *            The vector acting as 'Y' in the L * L^T * X = Y equation.
     */
    private void decompositionLU(ArrayList<ArrayList<BigDecimal>> matrixL,
            ArrayList<BigDecimal> vectorY) {
        this.matrixLTranspose = transposeMatrix(matrixL);
        ArrayList<BigDecimal> z = forwardPropagation(matrixL, vectorY);
        this.vectorA = backwardPropagation(matrixLTranspose, z);
    }

    /**
     * Performs forward propagation to calculate vector z. Essentially solves
     * the L * Z = Y equation.
     * 
     * @param matrixL
     *            The lower triangular matrix out of A matrix.
     * @param vectorY
     *            The vector of polynomial constants.
     * @return Vector z, used in the following steps to calculate vector x.
     */
    private ArrayList<BigDecimal> forwardPropagation(
            ArrayList<ArrayList<BigDecimal>> matrixL,
            ArrayList<BigDecimal> vectorY) {
        ArrayList<BigDecimal> z = new ArrayList<BigDecimal>();

        for (int i = 0; i < N; i++) {
            BigDecimal sum = BigDecimal.ZERO;
            for (int j = 0; j < i; j++) {
                sum = sum.add(matrixL.get(i).get(j).multiply(z.get(j)));
            }
            z.add(i, (vectorY.get(i).subtract(sum)).divide(matrixL.get(i)
                    .get(i), 50, RoundingMode.HALF_UP));
        }

        return z;
    }

    /**
     * Gets the approximate value of the function on argument (t_k - t_0),
     * where: * t_0 represents 08:00 of the same day as date parameter, * t_k
     * represents 07:59 of the following day.
     * 
     * @return Approximate value of the function.
     */
    public Double getApproximationValue(Double x) {
        return approximation(x).doubleValue();
    }

    /* *
     * Measures the difference between given timestamps as parameter and
     * referral timestamp in minutes as value x in symmetrical matrix.
     * 
     * @param key The timestamp to calculate difference between current and
     * referral.
     * 
     * @return
     */
    private BigInteger getX(Timestamp key) {
        return new BigInteger(Integer.toString((int) ((key.getTime() - this.t0
                .getTime()) / 60000L)));
    }

    /**
     * Initial empty (filled with 0.0) array of {@link BigDecimal}.
     * 
     * @param size
     *            The size of array to initialize.
     * @return Empty array size of given as parameter.
     */
    private ArrayList<BigDecimal> initEmptyArray(int size) {
        ArrayList<BigDecimal> array = new ArrayList<BigDecimal>();

        for (int i = 0; i < size; i++) {
            array.add(BigDecimal.ZERO);
        }

        return array;
    }

    /**
     * This method using given size, prepare an empty ArrayList of
     * ArrayList<BigDecimal>.
     * 
     * @param size
     *            The size to create empty ArrayList<ArrayList<BigDecimal>>.
     * @return Empty ArrayList of ArrayList<BigDecimal>.
     */
    private ArrayList<ArrayList<BigDecimal>> prepareArrayList(Integer size) {
        ArrayList<ArrayList<BigDecimal>> result = new ArrayList<ArrayList<BigDecimal>>();

        for (int i = 0; i < size; i++) {
            ArrayList<BigDecimal> row = new ArrayList<BigDecimal>();
            for (int j = 0; j < size; j++) {
                row.add(BigDecimal.ZERO);
            }
            result.add(row);
        }

        return result;
    }

    /**
     * This method calculate sum for current element in symmetrical matrix.
     * 
     * @param i
     *            The power for the value of x.
     * @param j
     *            The power for the value of x.
     * @return Sum for all elements in collection.
     */
    private BigInteger sum(Integer i, Integer j) {
        BigInteger result = new BigInteger("0");

        for (Entry<Timestamp, Float> x : dateCollection.entrySet()) {
            result = result.add(getX(x.getKey()).pow(i).multiply(
                    (getX(x.getKey()).pow(j))));
        }

        return result;
    }

    /**
     * Transposes the matrix given as a parameter.
     * 
     * @param matrixL
     *            Lower triangular matrix to be transposed as upper triangular
     *            matrix.
     * @return Transposed matrix.
     */
    private ArrayList<ArrayList<BigDecimal>> transposeMatrix(
            ArrayList<ArrayList<BigDecimal>> matrixL) {
        ArrayList<ArrayList<BigDecimal>> transponeMatrix = prepareArrayList(matrixL
                .size());
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                transponeMatrix.get(i).set(j, matrixL.get(j).get(i));
            }
        }

        return transponeMatrix;
    }

}
