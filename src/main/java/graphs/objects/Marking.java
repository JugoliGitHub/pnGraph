package graphs.objects;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A vector class. These vectors can only have positive integers. -1 stands for infinite. Used for
 * petrinets and corresponding classes.
 */
public class Marking implements Vector {

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

  public int getDimension() {
    return length;
  }

  public int get(int i) {
    return vectorArray[i];
  }

  @Override
  public Marking multiply(int factor) {
    if (factor < 0) {
      return new Marking(0);
    }
    return new Marking(
        Arrays.stream(vectorArray).map(j -> j < 0 ? -1 : j * factor).toArray());
  }

  @Override
  public Marking add(Vector marking) {
    if (marking.getDimension() != length) {
      return new Marking(0);
    }
    Marking result = new Marking(vectorArray.clone());
    for (int i = 0; i < length; i++) {
      if (result.vectorArray[i] == -1 || marking.get(i) == -1) {
        result.vectorArray[i] = -1;
      } else if (marking.get(i) >= 0 && result.vectorArray[i] >= 0) {
        result.vectorArray[i] += marking.get(i);
      } else {
        throw new IllegalArgumentException("Can´t add with negative values.");
      }
    }
    return result;
  }

  @Override
  public Marking add(int index, int value) {
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

  @Override
  public Marking sub(Vector marking) {
    if (this.getDimension() != marking.getDimension()) {
      return new Marking(0);
    }
    Marking result = new Marking(vectorArray.clone());
    for (int i = 0; i < length; i++) {
      result = result.sub(i, marking.get(i));
      if (result.length == 0) {
        return new Marking(0);
      }
    }
    return result;
  }

  @Override
  public Marking sub(int index, int value) {
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

  //TODO: allgemein anpassen
  @Override
  public boolean lessEquals(Vector marking) {
    return length == marking.getDimension()
        && IntStream.range(0, length)
        .noneMatch(i -> marking.get(i) != -1
            && (this.get(i) == -1 || !(this.get(i) <= marking.get(i))));
  }

  @Override
  public boolean lessThan(Vector marking) {
    return length == marking.getDimension() && !this.equals(marking) && this.lessEquals(marking);
  }

  @Override
  public boolean strictlyLessThan(Vector marking) {
    if (length == marking.getDimension()) {
      for (int i = 0; i < length; i++) {
        int thatInt = marking.get(i);
        if (!(this.vectorArray[i] < thatInt) || thatInt == -1) {
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
    if (this.getDimension() == waypoint.getDimension()) {
      for (int i = 0; i < this.getDimension(); i++) {
        if (this.get(i) == -1) {
          vectorArray[i] = -1;
        } else if (waypoint.get(i) != -1 && waypoint.get(i) < vectorArray[i]) {
          vectorArray[i] = -1;
        }
      }
    }
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
