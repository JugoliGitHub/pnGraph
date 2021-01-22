package graphs.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import exception.WrongDimensionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Class to test the Vector
 */
public class MarkingTest {

  Marking marking;
  Marking marking1;
  Marking marking2;
  Marking marking3;
  Marking marking4;

  @BeforeEach
  void prepare() {
    marking1 = new Marking(new int[]{1, 2, 3});
    marking2 = new Marking(new int[]{-1, 4, 3});
    marking3 = new Marking(new int[]{-1, 2});
    marking4 = new Marking(new int[]{2, -1});
  }

  @Test
  void createMarkingWithNoLengthFails() {
    try {
      marking = new Marking(0);
    } catch (IllegalArgumentException e) {
      assertEquals("The size must be at least 1.", e.getMessage());
    }
  }

  @Test
  void createMarkingWithNegativeLengthFails() {
    try {
      marking = new Marking(-1);
    } catch (IllegalArgumentException e) {
      assertEquals("The size must be at least 1.", e.getMessage());
    }
  }

  @Test
  void createMarkingWithNegativeValueFails() {
    try {
      marking = new Marking(1, -2);
    } catch (IllegalArgumentException e) {
      assertEquals("The value must be positive or -1 as omega.", e.getMessage());
    }
  }

  @Test
  void createMarkingWithOneValueWorks() {
    marking = new Marking(2, 0);

    assertEquals(2, marking.getDimension());
    assertEquals(0, marking.get(0));
    assertEquals(0, marking.get(1));
  }

  @Test
  void createNullMarkingWorks() {
    marking = new Marking(1);

    assertEquals(1, marking.getDimension());
    assertEquals(0, marking.get(0));
  }

  @Test
  void createMarkingWithArrayNegativeValuesFails() {
    try {
      marking = new Marking(new int[]{0, 1, -1, -2});
    } catch (IllegalArgumentException e) {
      assertEquals("All values must be positive or -1 as omega.", e.getMessage());
    }
  }

  @Test
  void createMarkingWithArrayWorks() {
    marking = new Marking(new int[]{0, 1, -1, 2});

    assertEquals(4, marking.getDimension());
    assertEquals(0, marking.get(0));
    assertEquals(1, marking.get(1));
    assertEquals(-1, marking.get(2));
    assertEquals(2, marking.get(3));
  }

  //other constructors

  //===== containsOmega() Tests =====//
  @Test
  void containsOmegaWorks() {
    assertFalse(marking1.containsOmega());
    assertTrue(marking2.containsOmega());
  }

  //===== lessEquals() Tests =====//
  @Test
  void lessEqualsWorksWithSameVector() {
    assertTrue(marking1.lessEquals(marking1.copy()));
  }

  @Test
  void lessEqualsWorksWithTwoVectors() {
    assertTrue(marking1.lessEquals(marking2));
    assertFalse(marking2.lessEquals(marking1));
  }

  @Test
  void lessEqualsWorksNotWithTwoOmegasAtDifferentPositions() {
    assertFalse(marking4.lessEquals(marking3));
    assertFalse(marking3.lessEquals(marking4));
  }

  @Test
  void lessEqualsWorksNotWithDifferentSize() {
    assertFalse(marking1.lessEquals(marking3));
  }

  //===== lessThan() Tests =====//
  @Test
  void lessThanWorksNotWithSameVector() {
    assertFalse(marking1.lessThan(marking1.copy()));
  }

  @Test
  void lessThanWorksWithTwoVectors() {
    assertTrue(marking1.lessThan(marking2));
    assertFalse(marking2.lessThan(marking1));
  }

  @Test
  void lessThanWorksNotWithTwoOmegasAtDifferentPositions() {
    assertFalse(marking4.lessThan(marking3));
    assertFalse(marking3.lessThan(marking4));
  }

  @Test
  void lessThanWorksNotWithDifferentSize() {
    assertFalse(marking1.lessThan(marking3));
  }

  //===== strictlyLessThan() Tests =====//
  @Test
  void strictlyLessThanWorksNotWithSameVector() {
    assertFalse(marking1.strictlyLessThan(marking1.copy()));
  }

  @Test
  void strictlyLessThanWorksWithTwoVectors() {
    marking = new Marking(new int[]{2, 3, 4});

    assertTrue(marking1.strictlyLessThan(marking));
    assertFalse(marking1.strictlyLessThan(marking2));
    assertFalse(marking2.strictlyLessThan(marking1));
  }

  @Test
  void strictlyLessThanWorksNotWithTwoOmegasAtDifferentPositions() {
    assertFalse(marking4.strictlyLessThan(marking3));
    assertFalse(marking3.strictlyLessThan(marking4));
  }

  @Test
  void strictlyLessThanWorksNotWithDifferentSize() {
    assertFalse(marking1.strictlyLessThan(marking3));
  }

  //===== isNotNegative() Tests =====//
  @Test
  void isNotNegativeWorks() {
    Marking markingNull = new Marking(new int[3]);
    marking = new Marking(new int[]{1, 0, 0});

    assertTrue(markingNull.isNotNegative());
    assertTrue(marking.isNotNegative());
    assertTrue(marking1.isNotNegative());
  }

  //===== isSemiPositive() Tests =====//
  @Test
  void isSemiPositiveWorks() {
    Marking markingNull = new Marking(new int[3]);
    marking = new Marking(new int[]{1, 0, 0});

    assertFalse(markingNull.isSemiPositive());
    assertTrue(marking.isSemiPositive());
    assertTrue(marking1.isSemiPositive());
  }

  //===== isStrictlyPositive() Tests =====//
  @Test
  void isStrictlyPositive() {
    Marking markingNull = new Marking(new int[3]);
    marking = new Marking(new int[]{1, 0, 0});

    assertFalse(markingNull.isStrictlyPositive());
    assertFalse(marking.isStrictlyPositive());
    assertTrue(marking1.isStrictlyPositive());
  }

  //TODO: add, sub ...

  //===== add() Tests =====//
  @Test
  void addingWithWrongDimensionThrowsError() {
    try {
      marking1.add(new Marking(new int[]{1, 2}));
    } catch (WrongDimensionException e) {
      assertEquals("The Vectors must have the same dimension to add them.", e.getMessage());
    }
  }

  @Test
  void addingWithNormalValuesWorks() {
    marking = (Marking) marking1.add(marking1);

    assertEquals(3, marking.getDimension());
    assertEquals(marking1.get(0) + marking1.get(0), marking.get(0));
    assertEquals(marking1.get(1) + marking1.get(1), marking.get(1));
    assertEquals(marking1.get(2) + marking1.get(2), marking.get(2));
  }

  @Test
  void addingWithOmegaWorks() {
    marking = (Marking) marking3.add(marking4);

    assertEquals(2, marking.getDimension());
    assertEquals(-1, marking.get(0));
    assertEquals(-1, marking.get(1));
  }

}
