package graphs.objects;

import exception.WrongDimensionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A matrix class.
 */
public class Matrix {

  private final IntVector[] rowArray;
  private final IntVector[] colArray;

  private final int n;
  private final int m;

  /**
   * Public constructor to create a matrix object of a two dimensional array.
   *
   * @param matrix two dimensional array
   */
  public Matrix(int[][] matrix) {
    n = matrix.length;
    m = matrix[0].length;
    rowArray = fillRows(matrix);
    colArray = fillColumns(matrix);
  }

  /**
   * Public constructor to create a matrix object with given rows or columns.
   *
   * @param rows the array of rows or columns
   * @param row  flag indicating when the first parameter are rows
   */
  public Matrix(IntVector[] rows, boolean row) {
    if (row) {
      this.n = rows[0].getDimension();
      this.m = rows.length;
      rowArray = rows;
      colArray = fillColumns(rows);
    } else {
      this.n = rows.length;
      this.m = rows[0].getDimension();
      colArray = rows;
      rowArray = fillRows(rows);
    }
  }

  private Matrix(IntVector[] rows, IntVector[] cols) {
    this.rowArray = rows;
    this.colArray = cols;
    this.n = rows[0].getDimension();
    this.m = rows.length;
  }

  private Matrix(Matrix left, Matrix right) {
    if (left.getM() != right.getM()) {
      throw new WrongDimensionException("The matrices must have the same dimension");
    }
    this.rowArray = IntStream.range(0, left.m).mapToObj(row -> {
      int[] newRow = new int[left.n + right.n];
      for (int i = 0; i < left.n || i < right.n; i++) {
        if (i < left.n) {
          newRow[i] = left.get(row, i);
        }
        if (i < right.n) {
          newRow[i + left.n] = right.get(row, i);
        }
      }
      return new IntVector(newRow);
    }).toArray(IntVector[]::new);
    this.n = rowArray[0].getDimension();
    this.m = rowArray.length;
    colArray = fillColumns(rowArray);
  }

  /**
   * Creates a identity matrix object. That means every number on the diagonal is a one and every
   * other number in the matrix is a zero.
   *
   * @param dimension width and height dimension
   * @return the identity matrix
   */
  public static Matrix identityMatrix(int dimension) {
    return new Matrix(IntStream.range(0, dimension)
        .mapToObj(row -> IntStream.range(0, dimension).map(col -> row == col ? 1 : 0).toArray())
        .toArray(int[][]::new));
  }

  private IntVector[] fillRows(int[][] matrix) {
    return IntStream.range(0, m).mapToObj(row ->
        new IntVector(matrix[row])).toArray(IntVector[]::new);
  }

  private IntVector[] fillRows(Vector[] cols) {
    return IntStream.range(0, m)
        .mapToObj(row -> new IntVector(IntStream.range(0, n).map(col ->
            cols[col].get(row)).toArray()))
        .toArray(IntVector[]::new);
  }

  private IntVector[] fillColumns(int[][] matrix) {
    return IntStream.range(0, n)
        .mapToObj(
            col -> new IntVector(IntStream.range(0, m).map(row -> matrix[row][col]).toArray()))
        .toArray(IntVector[]::new);
  }

  private IntVector[] fillColumns(Vector[] rows) {
    return IntStream.range(0, n)
        .mapToObj(
            col -> new IntVector(IntStream.range(0, m).map(row -> rows[row].get(col)).toArray()))
        .toArray(IntVector[]::new);
  }

  public int getM() {
    return m;
  }

  public int getN() {
    return n;
  }

  /**
   * Returns the row with the given row.
   *
   * @param s index of the row
   * @return the vector
   */
  public IntVector getRow(int s) {
    if (s < 0 || s >= m) {
      return new IntVector(m);
    }
    return rowArray[s];
  }

  public IntVector getColumn(int t) {
    if (t < 0 || t >= n) {
      return new IntVector(n);
    }
    return rowArray[t];
  }

  //TODO: check bounding
  public int get(int s, int t) {
    return rowArray[s].get(t);
  }

  //todo: traeger

  public Matrix transposed() {
    return new Matrix(colArray, rowArray);
  }

  public Matrix copy() {
    return new Matrix(rowArray.clone(), colArray.clone());
  }

  private void printStep(Matrix di, int step) {
    StringBuilder stepString = new StringBuilder(
        "------------------------------------- i = " + step + "\n");
    IntStream.range(0, di.m).forEach(row -> {
      IntStream.range(0, n)
          .map(num -> di.get(row, num))
          .forEach(num -> stepString.append(num < 0 ? (num + " ") : (" " + num + " ")));
      stepString.append("|");
      IntStream.range(n, di.n)
          .map(num -> di.get(row, num))
          .forEach(num -> stepString.append(num < 0 ? num + " " : " " + num + " "));
      stepString.append("\n");
    });

    System.out.println(stepString.toString());
  }

  /**
   * Calculates the minimal invariants of this matrix.
   *
   * @return list of invariants, can be empty
   */
  public List<Vector> minInvariants() {
    Matrix di = new Matrix(this.copy(), identityMatrix(m));
    printStep(di, 0);
    for (int i = 0; i < n; i++) {
      List<IntVector> nextStepMatrix = new ArrayList<>();
      List<Integer> leftRowsIndices = new ArrayList<>();
      for (int j = 0; j < di.getM(); j++) {
        if (di.get(j, i) == 0) {
          nextStepMatrix.add(di.rowArray[j]);
        } else {
          leftRowsIndices.add(j);
        }
      }
      for (Integer j : leftRowsIndices) {
        for (Integer k : leftRowsIndices) {
          if (di.get(j, i) * di.get(k, i) < 0) {
            IntVector w = (IntVector) di.rowArray[j].multiply(Math.abs(di.get(k, i)))
                .add(di.rowArray[k].multiply(Math.abs(di.get(j, i))));
            w = (IntVector) w.multiply(1 / IntVector.ggT(w));
            IntVector finalW = (IntVector) w.copy();
            if (nextStepMatrix.stream().noneMatch(row -> row.equals(finalW))) {
              nextStepMatrix.add(w);
            }
          }
        }
      }
      di = new Matrix(nextStepMatrix.toArray(IntVector[]::new), true);
      printStep(di, i + 1);
    }
    return Arrays.stream(di.rowArray)
        .map(row -> new IntVector(IntStream.range(0, m).map(i -> row.get(i + n)).toArray()))
        .collect(Collectors.toList());
  }

  @Override
  public String toString() {
    //TODO: implement
    StringBuilder stepString = new StringBuilder(
        "------------------------------------- \n");
    IntStream.range(0, this.m).forEach(row -> {
      IntStream.range(0, n)
          .map(num -> this.get(row, num))
          .forEach(num -> stepString.append(num < 0 ? (num + " ") : (" " + num + " ")));
      stepString.append("\n");
    });
    return stepString.toString();
  }
}
