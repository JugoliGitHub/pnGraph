package graphs.objects;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A vector class. These vectors can only have positive integers. -1 stands for infinite. Used for
 * petrinets and corresponding classes.
 */
public class Vector {

  private final int[] vectorArray;
  private final int length;

  /**
   * Creates a null vector of a specific length.
   *
   * @param length the length of the new vector
   */
  public Vector(int length) {
    this(length, 0);
  }

  /**
   * Creates a vector of a specific length with only one value.
   *
   * @param length the length of the new vector
   * @param value  the value
   */
  public Vector(int length, int value) {
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
  public Vector(int[] array) {
    this.vectorArray = array;
    this.length = vectorArray.length;
  }

  /**
   * Creates a vector of a vector string, returned of the toString() Method.
   *
   * @param vector the string of a vector: e.g. (1, 0)
   */
  public Vector(String vector) {
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
  public Vector(String[] markings) {
    this(Stream.of(markings).mapToInt(Integer::parseInt).toArray());
  }

  public int getLength() {
    return length;
  }

  public int get(int i) {
    return vectorArray[i];
  }

  /**
   * Compares two markings of less-/equality. Two markings are smaller than/ equal, when their
   * length is equal and for every pair of numbers a, b applies: a <= b.
   *
   * @param vector to compare to
   * @return true when this vector is less/equal than the other
   */
  public boolean lessEquals(Vector vector) {
    return length == vector.getLength()
        && IntStream.range(0, length)
        .allMatch(i -> this.get(i) <= vector.get(i));
  }

  /**
   * Compares two markings. Two markings are equal, when their length is equal * and for every pair
   * of numbers a, b applies: a <= b.
   *
   * @param vector to compare to
   * @return true when this vector is less than the other
   */
  public boolean lessThan(Vector vector) {
    return length == vector.getLength() && !this.equals(vector) && this.lessEquals(vector);
  }

  /**
   * Compares this vector to the given parameter.
   *
   * @param vector to compare to
   * @return true when this vector is strictly less than the other (<<)
   */
  public boolean strictlyLessThan(Vector vector) {
    return length == vector.length && IntStream.range(0, length)
        .allMatch(i -> this.get(i) < vector.get(i));
  }

  /**
   * Checks if this marking is not negative.
   *
   * @return true, when this is not negative
   */
  public boolean isNotNegative() {
    return new Vector(length).lessEquals(this);
  }

  /**
   * Checks if this mark is semi-positive.
   *
   * @return true, when this is semi-positive
   */
  public boolean isSemiPositive() {
    return new Vector(length).lessThan(this);
  }

  /**
   * Checks if this mark is strictly positive.
   *
   * @return true, when this is strictly positive
   */
  public boolean isStrictlyPositive() {
    return new Vector(length).strictlyLessThan(this);
  }

  /**
   * Returns the sum of this vector with another.
   *
   * @param vector additional vector
   */
  public Vector add(Vector vector) {
    if (vector.length != length) {
      return new Vector(0);
    }
    Vector result = new Vector(vectorArray.clone());
    IntStream.range(0, length).forEach(i -> result.vectorArray[i] += vector.get(i));
    return result;
  }

  /**
   * Returns the difference between this vector and the parameter.
   *
   * @param vector vector to subtract
   * @return the difference or an empty vector if it did not work
   */
  public Vector sub(Vector vector) {
    if (this.length != vector.length) {
      return new Vector(0);
    }
    Vector result = new Vector(vectorArray.clone());
    for (int i = 0; i < length; i++) {
      result = result.subAtIndex(i, vector.get(i));
      if (result.length == 0) {
        return new Vector(0);
      }
    }
    return result;
  }

  /**
   * Returns a new vector with an added value at an index.
   *
   * @param index index bigger 0 and less than length of this vector
   * @param value integer bigger 0
   * @return the new vector or an empty vector
   */
  public Vector addAtIndex(int index, int value) {
    if (index < vectorArray.length && index > 0) {
      int[] newArray = vectorArray.clone();
      newArray[index] += value;
      return new Vector(newArray);
    }
    return new Vector(0);
  }

  /**
   * Returns a new vector with the subtracted value at an index.
   *
   * @param index index bigger 0 and less than length of this vector
   * @param value integer bigger 0
   * @return the new vector or an empty vector
   */
  public Vector subAtIndex(int index, int value) {
    if (index < vectorArray.length) {
      int[] tmpArray = vectorArray.clone();
      tmpArray[index] -= value;
      return new Vector(tmpArray);
    }
    return new Vector(0);
  }

  /**
   * Returns a copy of this vector.
   *
   * @return a new marking with the same values
   */
  public Vector copy() {
    return new Vector(vectorArray.clone());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Vector)) {
      return false;
    }

    Vector vector = (Vector) o;

    if (length != vector.length) {
      return false;
    }
    return Arrays.equals(vectorArray, vector.vectorArray);
  }

  @Override
  public String toString() {
    StringBuilder toReturn = new StringBuilder("(");
    Arrays.stream(vectorArray).forEach(value -> toReturn.append(value).append(","));
    toReturn.deleteCharAt(toReturn.length() - 1);
    toReturn.append(")");
    return toReturn.toString();
  }
}