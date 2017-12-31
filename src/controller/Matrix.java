package controller;

public class Matrix {

    private double matrix[][];
    private int column;
    private int row;

    /**
     * @param rows    Number of Matrix' rows
     * @param columns Number of Matrix' columns
     */
    public Matrix(int rows, int columns) {
        this.row = rows;
        this.column = columns;
        this.matrix = new double[rows][columns];
        this.fillRandom();
    }

    public Matrix(double[][] matrixRes) {
        this.row = matrixRes.length;
        this.column = matrixRes[0].length;
        this.matrix = matrixRes;
    }


    /**
     * This method feeds the matrix with random values
     *
     * @param matrix  The matrix
     * @param rows    Number of Matrix' rows
     * @param columns Number of Matrix' columns
     */
    public void fillRandom() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                matrix[i][j] = Math.random();
            }
        }
    }

    /**
     * Get a new matrix which is transposed.
     *
     * @return
     */
    public Matrix transpose() {
        double[][] values = new double[column][row];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                values[j][i] = this.matrix[i][j];
            }
        }
        return new Matrix(values);
    }

    public double[] getRow(int i) {
        return this.matrix[i];
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public void setRow(int i, double[] input) {
        this.matrix[i] = input;
    }

    public void setValue(int m, int n, double v) {
        this.matrix[m][n] = v;
    }

    public int getColumns() {
        return column;
    }

    public int getRows() {
        return row;
    }

    public double getValue(int i, int j) {
        return this.matrix[i][j];
    }

}
