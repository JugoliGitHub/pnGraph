package graphs.objects;

import java.util.stream.IntStream;

public class Matrix {

  public final Vector[] rowArray;
  public final Vector[] colArray;

  private final int n;
  private final int m;

  public Matrix(int[][] matrix) {
    n = matrix.length;
    m = matrix[0].length;
    rowArray = fillRows(matrix);
    colArray = fillColumns(matrix);
  }

  private Matrix(Vector[] rows, Vector[] cols) {
    this.rowArray = rows;
    this.colArray = cols;
    this.n = rows.length;
    this.m = cols.length;
  }

  private Vector[] fillRows(int[][] matrix) {
    return IntStream.range(0, m).mapToObj(row -> new Vector(matrix[row])).toArray(Vector[]::new);
  }

  private Vector[] fillColumns(int[][] matrix) {
    return IntStream.range(0, n)
        .mapToObj(col -> new Vector(IntStream.range(0, m).map(row -> matrix[row][col]).toArray()))
        .toArray(Vector[]::new);
  }

  public int getM() {
    return m;
  }

  public int getN() {
    return n;
  }

  public Vector getRow(int s) {
    if (s < 0 || s >= m) {
      return new Vector(m);
    }
    return rowArray[s];
  }

  public Vector getColumn(int t) {
    if (t < 0 || t >= n) {
      return new Vector(n);
    }
    return rowArray[t];
  }

  //todo: traeger

  public Matrix transposed() {
    return new Matrix(colArray, rowArray);
  }

}
