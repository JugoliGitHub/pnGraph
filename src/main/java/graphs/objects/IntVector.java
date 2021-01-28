package graphs.objects;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A vector class. These vectors can only have positive integers. -1 stands for infinite. Used for
 * petrinets and corresponding classes.
 */
public class IntVector implements Vector {

  private final int[] vectorArray;
  private final int length;

  /**
   * Creates a null vector of a specific length.
   *
   * @param length the length of the new vector
   */
  public IntVector(int length) {
    this(length, 0);
  }

  /**
   * Creates a vector of a specific length with only one value.
   *
   * @param length the length of the new vector
   * @param value  the value
   */
  public IntVector(int length, int value) {
    if (length < 0) {
      throw new IllegalArgumentException("The size must be at least 1.");
    }
    this.length = length;
    this.vectorArray = new int[length];
    for (int i = 0; i < length; i++) {
      this.vectorArray[i] = value;
    }
  }

  /**
   * Creates a vector of a given array.
   *
   * @param array integer array
   */
  public IntVector(int[] array) {
    this.vectorArray = array;
    this.length = vectorArray.length;
  }

  /**
   * Creates a vector of a vector string, returned of the toString() Method.
   *
   * @param vector the string of a vector: e.g. (1, 0)
   */
  public IntVector(String vector) {
    vector = vector.replace("(", "").replace(")", "");
    String[] valuesAsString = vector.split(", ");
    vectorArray = new int[valuesAsString.length];
    this.length = valuesAsString.length;
    for (int i = 0; i < valuesAsString.length; i++) {
      String valueAsString = valuesAsString[i];
      vectorArray[i] = Integer.parseInt(valueAsString);
    }
  }

  /**
   * Creates a vector of a string array.
   *
   * @param markings string array
   */
  public IntVector(String[] markings) {
    this(Stream.of(markings).mapToInt(Integer::parseInt).toArray());
  }

  public IntVector() {
    this(0);
  }

  public static int ggT(IntVector vector) {
    int a = vector.vectorArray[0];
    int b = 0;
    for (int i = 1; i < vector.length; i++) {
      b = vector.vectorArray[i];
      while (b != 0) {
        b = a % (a = b);
      }
    }
    return a;
  }

  public int getDimension() {
    return length;
  }

  @Override
  public int get(int i) {
    return vectorArray[i];
  }

  @Override
  public IntVector multiply(int factor) {
    return new IntVector(
        Arrays.stream(vectorArray).map(j -> j * factor).toArray());
  }

  @Override
  public IntVector add(Vector vector) {
    if (vector.getDimension() != length) {
      return new IntVector(0);
    }
    return new IntVector(
        IntStream.range(0, length).map(i -> this.get(i) + vector.get(i)).toArray());
  }

  @Override
  public IntVector add(int index, int value) {
    if (index < vectorArray.length && index > 0) {
      int[] newArray = vectorArray.clone();
      newArray[index] += value;
      return new IntVector(newArray);
    }
    return new IntVector(0);
  }

  @Override
  public IntVector sub(Vector vector) {
    if (this.getDimension() != vector.getDimension()) {
      return new IntVector(0);
    }
    return new IntVector(
        IntStream.range(0, length).map(i -> this.get(i) - vector.get(i)).toArray());
  }


  @Override
  public IntVector sub(int index, int value) {
    if (index < vectorArray.length) {
      int[] tmpArray = vectorArray.clone();
      tmpArray[index] -= value;
      return new IntVector(tmpArray);
    }
    return new IntVector(0);
  }

  @Override
  public boolean lessEquals(Vector vector) {
    return length == vector.getDimension()
        && IntStream.range(0, length)
        .allMatch(i -> this.get(i) <= vector.get(i));
  }

  @Override
  public boolean lessThan(Vector vector) {
    return length == vector.getDimension() && !this.equals(vector) && this.lessEquals(vector);
  }

  @Override
  public boolean strictlyLessThan(Vector vector) {
    return length == vector.getDimension() && IntStream.range(0, length)
        .allMatch(i -> this.get(i) < vector.get(i));
  }

  @Override
  public boolean isNotNegative() {
    return new IntVector(length).lessEquals(this);
  }

  @Override
  public boolean isSemiPositive() {
    return new IntVector(length).lessThan(this);
  }

  @Override
  public boolean isStrictlyPositive() {
    return new IntVector(length).strictlyLessThan(this);
  }

  @Override
  public Vector copy() {
    return new IntVector(vectorArray.clone());
  }

  @Override
  public IntStream stream() {
    return Arrays.stream(vectorArray);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof IntVector)) {
      return false;
    }

    IntVector vector = (IntVector) o;

    if (length != vector.length) {
      return false;
    }
    return Arrays.equals(vectorArray, vector.vectorArray);
  }

  @Override
  public String toString() {
    StringBuilder toReturn = new StringBuilder("(");
    Arrays.stream(vectorArray).forEach(value -> toReturn.append(value).append(","));
    if (length > 0) {
      toReturn.deleteCharAt(toReturn.length() - 1);
    } else {
      toReturn.append(" ");
    }
    toReturn.append(")");
    return toReturn.toString();
  }

}