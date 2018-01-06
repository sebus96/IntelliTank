package controller;

public class MatrixOperations {


    /**
     * Matrix Multiplication
     *
     * @param mA First Input Matrix
     * @param mB Second Input Matrix
     * @return The result of the operation
     */
    public static Matrix matMult(Matrix mA, Matrix mB) {

        double[][] matrixA = mA.getMatrix();
        double[][] matrixB = mB.getMatrix();

        int rowsA = matrixA.length;
        int columnsA = matrixA[0].length;
        int rowsB = matrixB.length;
        int columnsB = matrixB[0].length;

        /**
         * Compares if the number of columns of the first matrix is different to the number of lines of the second Matrix. If yes, throws an IllegalException as it's
         * not possible to multiply the two matrixes.
         */
        if (columnsA != rowsB) {
            throw new IllegalArgumentException("The dimensions have to be equal!");
        }

        double[][] matrixRes = new double[rowsA][columnsB];

        /**
         * Walks through the matrixes, multiplying the elements and summing to the result element on the resultant Matrix matrixRes
         */
        for (int i = 0; i < rowsA; i++) { // aRow
            for (int j = 0; j < columnsB; j++) { // bColumn
                for (int k = 0; k < columnsA; k++) { // aColumn
                    matrixRes[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }

        return new Matrix(matrixRes);
    }

    /**
     * Matrix Sum
     *
     * @param mA First Input Matrix
     * @param mB Second Input Matrix
     * @return the result of the sum operation
     */
    public static Matrix matAdd(Matrix mA, Matrix mB) {

    	double[][] matrixA = mA.getMatrix();
    	double[][] matrixB = mB.getMatrix();

        int rowsA = matrixA.length;
        int columnsA = matrixA[0].length;
        int rowsB = matrixB.length;
        int columnsB = matrixB[0].length;

        /**
         * Compares if the dimensions of the matrixes are not exactly the same. If they aren't, throws an exception informing this.
         */
        if (columnsA != columnsB || rowsA != rowsB) {
            throw new IllegalArgumentException("The dimensions have to be equal!");
        }

        double[][] matrixRes = new double[rowsA][columnsB];
        /**
         * Walks through the matrixes, summing the elements and putting them on the resultant Matrix matrixRes
         */
        for (int i = 0; i < rowsA; i++) { // aRow
            for (int j = 0; j < columnsB; j++) { // bColumn

                matrixRes[i][j] = matrixA[i][j] + matrixB[i][j];

            }
        }

        return new Matrix(matrixRes);

    }

    /**
     * Matrix Sum
     *
     * @param mA First Input Matrix
     * @param mB Second Input Matrix
     * @return the result of the sum operation
     */
    public static Matrix matMult(double a, Matrix b) {

    	double[][] matrix = b.getMatrix();

        int rows = matrix.length;
        int columns = matrix[0].length;

        double[][] matrixRes = new double[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {

                matrixRes[i][j] = matrix[i][j] * a;

            }
        }

        return new Matrix(matrixRes);

    }

    /**
     * Shows the matrix as output
     *
     * @param m The matrix the user wants to show
     */
    public static void matOut(Matrix m) {

    	double[][] matrix = m.getMatrix();

        int rows = matrix.length;
        int columns = matrix[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {

                System.out.print(matrix[i][j] + "\t");

            }
            System.out.println();

        }
    }

    /**
     * Calculates for every element in the matrix the sigmoid.
     *
     * @param matrix
     * @return
     */
    public static Matrix sigmoid(Matrix matrix) {
        for (int m = 0; m < matrix.getMatrix().length; m++)
            for (int n = 0; n < matrix.getMatrix()[0].length; n++)
                matrix.setValue(m, n, (1.0 / (1.0 + Math.exp(-1 * matrix.getValue(m,n)))));
        return matrix;
    }

    /**
     * Calculates the sigmoid derivation of a matrix m.
     * @param m
     * @return
     */
    public static Matrix sigmoidDerivation(Matrix m){

        Matrix nonlin = sigmoid(m);
        Matrix m2 = new Matrix(m.getRows(), m.getColumns());

        // 1-x
        for (int i = 0; i < m2.getRows(); i++)
            for (int j = 0; j < m2.getColumns(); j++)
                m2.setValue(i, j, 1 - nonlin.getValue(i, j));

        return matMult(nonlin, m2.transpose());
    }

}
