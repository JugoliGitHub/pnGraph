package graphs.objects;

public interface Vector {

  int getDimension();

  int get(int i);

  Vector multiply(int factor);

  /**
   * Returns the sum of this vector with another.
   *
   * @param vector additional vector
   */
  Vector add(Vector vector);

  /**
   * Returns a new vector with an added value at an index.
   *
   * @param index index bigger 0 and less than length of this vector
   * @param value integer bigger 0
   * @return the new vector or an empty vector
   */
  Vector add(int index, int value);

  /**
   * Returns the difference between this vector and the parameter.
   *
   * @param vector vector to subtract
   * @return the difference or an empty vector if it did not work
   */
  Vector sub(Vector vector);

  /**
   * Returns a new vector with the subtracted value at an index.
   *
   * @param index index bigger 0 and less than length of this vector
   * @param value integer bigger 0
   * @return the new vector or an empty vector
   */
  Vector sub(int index, int value);

  /**
   * Compares two markings of less-/equality. Two markings are smaller than/ equal, when their
   * length is equal and for every pair of numbers a, b applies: a <= b.
   *
   * @param vector to compare to
   * @return true when this vector is less/equal than the other
   */
  boolean lessEquals(Vector vector);

  /**
   * Compares two markings. Two markings are equal, when their length is equal * and for every pair
   * of numbers a, b applies: a <= b.
   *
   * @param vector to compare to
   * @return true when this vector is less than the other
   */
  boolean lessThan(Vector vector);

  /**
   * Compares this vector to the given parameter.
   *
   * @param vector to compare to
   * @return true when this vector is strictly less than the other (<<)
   */
  boolean strictlyLessThan(Vector vector);

  /**
   * Checks if this marking is not negative.
   *
   * @return true, when this is not negative
   */
  boolean isNotNegative();

  /**
   * Checks if this mark is semi-positive.
   *
   * @return true, when this is semi-positive
   */
  boolean isSemiPositive();

  /**
   * Checks if this mark is strictly positive.
   *
   * @return true, when this is strictly positive
   */
  boolean isStrictlyPositive();

  /**
   * Returns a copy of this vector.
   *
   * @return a new marking with the same values
   */
  Vector copy();

}
