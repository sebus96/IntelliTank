package controller;

public class Matrix {

    private double matrix[][];
    private int column;
    private int row;

    /**
     * @param rows    Zeilenanzahl
     * @param columns Spaltenanzahl
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

    public void fillRandom() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                matrix[i][j] = Math.random();
            }
        }
    }

    /**
     * Gibt die transponierte Matrix zurück.
     *
     * @return transponierte Matrix
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

    @Override
    public String toString() {
    	String res = "";
        int rows = matrix.length;
        int columns = matrix[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                res += (matrix[i][j] + "\t");
            }
            res += "\n";
        }
        return res;
    }
}
