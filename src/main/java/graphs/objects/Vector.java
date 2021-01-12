package graphs.objects;

import exception.WrongDimensionException;
import java.util.Arrays;
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
    if (length <= 0) {
      throw new IllegalArgumentException("The size must be at least 1.");
    }
    if (value < -1) {
      throw new IllegalArgumentException("The value must be positive or -1 as omega.");
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
    if (Arrays.stream(array).noneMatch(v -> v < -1)) {
      this.vectorArray = array;
      this.length = vectorArray.length;
    } else {
      throw new IllegalArgumentException("All values must be positive or -1 as omega.");
    }
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
      if (Integer.parseInt(valueAsString) < -1) {
        throw new IllegalArgumentException("No negative values.");
      } else if (valueAsString.equals("ω") || valueAsString.equals("-1")) {
        vectorArray[i] = -1;
      } else {
        vectorArray[i] = Integer.parseInt(valueAsString);
      }
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

  public int[] getVectorArray() {
    return vectorArray;
  }

  public boolean containsOmega() {
    return Arrays.stream(vectorArray).anyMatch(i -> i == -1);
  }

  /**
   * Compares this vector to the given parameter.
   *
   * @param vector to compare to
   * @return true when this vector is less than the other
   */
  public boolean lessThan(Vector vector) {
    if (length == vector.getLength() && !this.equals(vector)) {
      for (int i = 0; i < length; i++) {
        int thisInt = this.vectorArray[i];
        int thatInt = vector.get(i);
        if (thatInt != -1 && thisInt != -1 && thisInt >= thatInt) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Compares this vector to the given parameter.
   *
   * @param vector to compare to
   * @return true when this vector is less than the other
   */
  public boolean lessEquals(Vector vector) {
    if (length == vector.getLength()) {
      if (this.equals(vector)) {
        return true;
      }
      for (int i = 0; i < length; i++) {
        int thisInt = this.vectorArray[i];
        int thatInt = vector.get(i);
        if (thatInt != -1 && thisInt != -1 && thisInt > thatInt) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Returns the sum of this vector with another.
   *
   * @param vector additional vector
   */
  public Vector add(Vector vector) throws WrongDimensionException {
    if (vector.length == length) {
      Vector temp = new Vector(vectorArray.clone());
      for (int i = 0; i < length; i++) {
        if (temp.vectorArray[i] == -1) {
          continue;
        }
        if (vector.get(i) >= 0) {
          if (temp.vectorArray[i] >= 0) {
            temp.vectorArray[i] += vector.get(i);
          }
        } else if (vector.get(i) == -1) {
          temp.vectorArray[i] = -1;
        } else {
          throw new IllegalArgumentException("Can´t add with omega or negative values.");
        }
      }
      return temp;
    } else {
      throw new WrongDimensionException("The Vectors must have the same dimension to add them.");
    }
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
    Vector temp = new Vector(vectorArray.clone());
    for (int i = 0; i < length; i++) {
      temp = temp.subAtIndex(i, vector.get(i));
      if (temp.length == 0) {
        return new Vector(0);
      }
    }
    return temp;
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
      if (value < 0 || vectorArray[index] != -1) {
        return new Vector(0);
      } else {
        int[] newArray = vectorArray.clone();
        newArray[index] += value;
        if (newArray[index] >= 0) {
          return new Vector(newArray);
        }
      }
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
      if (vectorArray[index] != -1) {
        int[] tmpArray = vectorArray.clone();
        if (value < 0 || tmpArray[index] < value) {
          return new Vector(0);
        } else {
          tmpArray[index] -= value;
        }
        return new Vector(tmpArray);
      }
    }
    return new Vector(0);
  }

  /**
   * Returns a new vector with a value of omega at index.
   *
   * @param index integer between 0 (included) and the length (exclueded)
   * @return the new vector or an empty if operation not allowed
   */
  public Vector setOmega(int index) {
    if (index >= 0 && index < length) {
      int[] tmpVector = vectorArray.clone();
      tmpVector[index] = -1;
      return new Vector(tmpVector);
    }
    return new Vector(0);
  }

  public Vector copy() {
    return new Vector(vectorArray.clone());
  }

  @Override
  public String toString() {
    StringBuilder toReturn = new StringBuilder("(");
    Arrays.stream(vectorArray).forEach(value -> {
      if (value == -1) {
        toReturn.append("ω,");
      } else {
        toReturn.append(value).append(",");
      }
    });
    toReturn.deleteCharAt(toReturn.length() - 1);
    toReturn.append(")");
    return toReturn.toString();
  }
}
