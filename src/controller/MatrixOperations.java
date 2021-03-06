package controller;

public class MatrixOperations {


    /**
     * Matrix Multiplikation
     *
     * @param mA erste Matrix
     * @param mB zweite Matrix
     * @return Ergebnis
     */
    public static Matrix matMult(Matrix mA, Matrix mB) {

        double[][] matrixA = mA.getMatrix();
        double[][] matrixB = mB.getMatrix();

        int rowsA = matrixA.length;
        int columnsA = matrixA[0].length;
        int rowsB = matrixB.length;
        int columnsB = matrixB[0].length;

        if (columnsA != rowsB) {
            throw new IllegalArgumentException("The dimensions have to be equal! (" + columnsA + " " + rowsB + ")");
        }

        double[][] matrixRes = new double[rowsA][columnsB];

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
     * Matrix Summe
     *
     * @param mA erste Matrix
     * @param mB zweite Matrix
     * @return Summe
     */
    public static Matrix matAdd(Matrix mA, Matrix mB) {

    	double[][] matrixA = mA.getMatrix();
    	double[][] matrixB = mB.getMatrix();

        int rowsA = matrixA.length;
        int columnsA = matrixA[0].length;
        int rowsB = matrixB.length;
        int columnsB = matrixB[0].length;

        if (columnsA != columnsB || rowsA != rowsB) {
            throw new IllegalArgumentException("The dimensions have to be equal!");
        }

        double[][] matrixRes = new double[rowsA][columnsB];
        
        for (int i = 0; i < rowsA; i++) { // aRow
            for (int j = 0; j < columnsB; j++) { // bColumn

                matrixRes[i][j] = matrixA[i][j] + matrixB[i][j];

            }
        }

        return new Matrix(matrixRes);

    }

    /**
     * Multiplikation mit Matrix.
     *
     * @param a Faktor
     * @param b Matrix
     * @return Ergebnis
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
     * Berechnet den Sigmoid jedes Eintrags
     *
     * @param matrix Matrix
     * @return Ergebnis
     */
    public static Matrix sigmoid(Matrix matrix) {
        for (int m = 0; m < matrix.getMatrix().length; m++)
            for (int n = 0; n < matrix.getMatrix()[0].length; n++)
                matrix.setValue(m, n, (1.0 / (1.0 + Math.exp(-1 * matrix.getValue(m,n)))));
        return matrix;
    }

    /**
     * Berechnet den Sigmoid Ableitung jedes Eintrags
     * 
     * @param m Matrix
     * @return Ergebnis
     */
    public static Matrix sigmoidDerivation(Matrix m){

        Matrix nonlin = sigmoid(m); // axb
        Matrix m2 = new Matrix(m.getRows(), m.getColumns()); //axb

        // 1-x
        for (int i = 0; i < m2.getRows(); i++)
            for (int j = 0; j < m2.getColumns(); j++)
                m2.setValue(i, j, 1 - nonlin.getValue(i, j));

        return matMult(nonlin, m2.transpose()); // axb * bxa = axa
    }

}
