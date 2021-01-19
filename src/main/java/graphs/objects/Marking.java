package graphs.objects;

import exception.WrongDimensionException;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A vector class. These vectors can only have positive integers. -1 stands for infinite. Used for
 * petrinets and corresponding classes.
 */
public class Marking {

  private final int[] vectorArray;
  private final int length;

  /**
   * Creates a null vector of a specific length.
   *
   * @param length the length of the new vector
   */
  public Marking(int length) {
    this(length, 0);
  }

  /**
   * Creates a vector of a specific length with only one value.
   *
   * @param length the length of the new vector
   * @param value  the value
   */
  public Marking(int length, int value) {
    if (length < 0) {
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
  public Marking(int[] array) {
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
  public Marking(String vector) {
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
  public Marking(String[] markings) {
    this(Stream.of(markings).mapToInt(Integer::parseInt).toArray());
  }

  public int getLength() {
    return length;
  }

  public int get(int i) {
    return vectorArray[i];
  }

  /**
   * Checks whether a omega-value is in this marking.
   *
   * @return true when omega in marking
   */
  public boolean containsOmega() {
    return Arrays.stream(vectorArray).anyMatch(i -> i == -1);
  }

  /**
   * Sets omega-values, when an index of the waypoint is smaller than this index at this marking.
   *
   * @param waypoint another marking to compare to
   */
  public void setOmegas(Marking waypoint) {
    if (this.getLength() == waypoint.getLength()) {
      for (int i = 0; i < this.getLength(); i++) {
        if (this.get(i) == -1) {
          vectorArray[i] = -1;
        } else if (waypoint.get(i) != -1 && waypoint.get(i) < vectorArray[i]) {
          vectorArray[i] = -1;
        }
      }
    }
  }

  /**
   * Compares two markings of less-/equality. Two markings are smaller than/ equal, when their length is equal
   * and for every pair of numbers a, b applies: a <= b.
   *
   * @param marking to compare to
   * @return true when this vector is less/equal than the other
   */
  public boolean lessEquals(Marking marking) {
    if (length == marking.getLength()) {
      for (int i = 0; i < length; i++) {
        if (this.get(i) == -1 && marking.get(i) != -1) {
          return false;
        }
        if (this.get(i) != -1 && marking.get(i) == -1) {
          break;
        }
        if (this.get(i) != -1 && this.get(i) > marking.get(i)) {
          return false;
        }

      }
      return true;
    }
    return false;
  }

  /**
   * Compares two markings. Two markings are equal, when their length is equal
   *    * and for every pair of numbers a, b applies: a <= b.
   *
   * @param marking to compare to
   * @return true when this vector is less than the other
   */
  public boolean lessThan(Marking marking) {
    if (length == marking.getLength() && !this.equals(marking)) {
      if (this.equals(marking)) {
        return false;
      }
      return lessEquals(marking);
    }
    return false;
  }

  /**
   * Compares this vector to the given parameter.
   *
   * @param marking to compare to
   * @return true when this vector is strictly less than the other (<<)
   */
  public boolean strictlyLessThan(Marking marking) {
    if (length == marking.getLength()) {
      for (int i = 0; i < length; i++) {
        int thisInt = this.vectorArray[i];
        int thatInt = marking.get(i);
        if (!(thisInt < thatInt) || thatInt == -1) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Checks if this marking is not negative.
   *
   * @return true, when this is not negative
   */
  public boolean isNotNegative() {
    return new Marking(length).lessEquals(this);
  }

  /**
   * Checks if this mark is semi-positive.
   *
   * @return true, when this is semi-positive
   */
  public boolean isSemiPositive() {
    return new Marking(length).lessThan(this);
  }

  /**
   * Checks if this mark is strictly positive.
   *
   * @return true, when this is strictly positive
   */
  public boolean isStrictlyPositive() {
    return new Marking(length).strictlyLessThan(this);
  }

  /**
   * Returns the sum of this vector with another.
   *
   * @param marking additional vector
   */
  public Marking add(Marking marking) throws WrongDimensionException {
    if (marking.length == length) {
      Marking result = new Marking(vectorArray.clone());
      for (int i = 0; i < length; i++) {
        if (result.vectorArray[i] == -1) {
          continue;
        }
        if (marking.get(i) >= 0) {
          if (result.vectorArray[i] >= 0) {
            result.vectorArray[i] += marking.get(i);
          }
        } else if (marking.get(i) == -1) {
          result.vectorArray[i] = -1;
        } else {
          throw new IllegalArgumentException("Can´t add with negative values.");
        }
      }
      return result;
    } else {
      throw new WrongDimensionException("The Vectors must have the same dimension to add them.");
    }
  }

  /**
   * Returns the difference between this vector and the parameter.
   *
   * @param marking vector to subtract
   * @return the difference or an empty vector if it did not work
   */
  public Marking sub(Marking marking) {
    if (this.length != marking.length) {
      return new Marking(0);
    }
    Marking result = new Marking(vectorArray.clone());
    for (int i = 0; i < length; i++) {
      result = result.subAtIndex(i, marking.get(i));
      if (result.length == 0) {
        return new Marking(0);
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
  public Marking addAtIndex(int index, int value) {
    if (index < vectorArray.length && index > 0) {
      if (value < 0 || vectorArray[index] != -1) {
        return new Marking(0);
      } else {
        int[] newArray = vectorArray.clone();
        newArray[index] += value;
        if (newArray[index] >= 0) {
          return new Marking(newArray);
        }
      }
    }
    return new Marking(0);
  }

  /**
   * Returns a new vector with the subtracted value at an index.
   *
   * @param index index bigger 0 and less than length of this vector
   * @param value integer bigger 0
   * @return the new vector or an empty vector
   */
  public Marking subAtIndex(int index, int value) {
    if (index < vectorArray.length) {
      int[] tmpArray = vectorArray.clone();
      if (vectorArray[index] != -1) {
        if (value < 0 || tmpArray[index] < value) {
          return new Marking(0);
        } else {
          tmpArray[index] -= value;
        }
      }
      return new Marking(tmpArray);
    }
    return new Marking(0);
  }

  /**
   * Returns a new vector with a value of omega at index.
   *
   * @param index integer between 0 (included) and the length (excluded)
   * @return the new vector or an empty if operation not allowed
   */
  public Marking setOmega(int index) {
    if (index >= 0 && index < length) {
      int[] tmpVector = vectorArray.clone();
      tmpVector[index] = -1;
      return new Marking(tmpVector);
    }
    return new Marking(0);
  }

  /**
   * Returns a copy of this vector.
   *
   * @return a new marking with the same values
   */
  public Marking copy() {
    return new Marking(vectorArray.clone());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Marking)) {
      return false;
    }

    Marking marking = (Marking) o;

    if (length != marking.length) {
      return false;
    }
    return Arrays.equals(vectorArray, marking.vectorArray);
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
