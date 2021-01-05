package graph;

import java.util.Arrays;
import exception.WrongDimensionException;
import java.util.stream.Stream;

public class Vector {

  private int[] vectorArray;
  private int length;

  /**
   * Constructors
   */
  public Vector(int length) {
    this.length = length;
    vectorArray = new int[length];
  }

  public Vector(int length, int value) {
    this(length);
    for(int i = 0; i < length; i++) {
      this.vectorArray[i] = value;
    }
  }

  public Vector(int[] array) {
    if (Arrays.stream(array).noneMatch(v -> v < -1)) {
      this.vectorArray = array;
      this.length = vectorArray.length;
    }
  }

  public Vector(String vector) {
    vector = vector.replace("(", "").replace(")", "");
    String[] valuesAsString = vector.split(", ");
    vectorArray = new int[valuesAsString.length];
    for (int i = 0; i < valuesAsString.length; i++) {
      String valueAsString = valuesAsString[i];
      if (Integer.parseInt(valueAsString) < -1) {
        throw new IllegalArgumentException("No negative values.");
      } else if (valueAsString.equals("ω") || valueAsString.equals("-1")) {
        vectorArray[i] = Integer.parseInt(valueAsString);
      } else {
        vectorArray[i] = Integer.parseInt(valueAsString);
      }
    }
  }

  public Vector(String[] markings) {
    this(Stream.of(markings).mapToInt(Integer::parseInt).toArray());
  }

  public int getLength() {
    return length;
  }

  public int get(int i) {
    if (i < length) {
      return vectorArray[i];
    } else {
      throw new ArrayIndexOutOfBoundsException();
    }
  }

  public int[] getVectorArray() {
    return vectorArray;
  }

  public boolean containsOmega() {
    return Arrays.stream(vectorArray).anyMatch(i -> i == -1);
  }

  /**
   * Compares whether another vector equals this.
   *
   * @param vector the other vector
   * @return true when the values are equal
   */
  public boolean equals(Vector vector) {
    return Arrays.equals(vectorArray, vector.getVectorArray());
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
      if (this.equals(vector)) return true;
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
        if(temp.vectorArray[i] == -1) continue;
        if (vector.get(i) >= 0) {
          if(temp.vectorArray[i] >= 0) {
            temp.vectorArray[i] += vector.get(i);
          }
        } else if(vector.get(i) == -1) {
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
   *
   * @param vector
   * @return the difference or an empty vector if it did not work
   */
  public Vector sub(Vector vector) {
    if(this.length != vector.length) {
      return new Vector(0);
    }
    Vector temp = new Vector(vectorArray.clone());
    for(int i = 0; i < length; i++) {
      if(!temp.subAtIndex(i, vector.get(i))) {
        return new Vector(0);
      }
    }
    return temp;
  }

  public boolean addAtIndex(int index, int value) {
    if(index < vectorArray.length) {
      if(vectorArray[index] != -1) {
        if(value < 0) {
          return false;
        } else {
          vectorArray[index] += value;
        }
      }
      return true;
    }
    return false;
  }

  public boolean subAtIndex(int index, int value) {
    if(index < vectorArray.length) {
      if(vectorArray[index] != -1) {
        if(value < 0 || vectorArray[index] < value) {
          return false;
        } else {
          vectorArray[index] -= value;
        }
      }
      return true;
    }
    return false;
  }

  public void setOmega(int index) {
    if(index >= 0 && index < length) {
      vectorArray[index] = -1;
    }
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
