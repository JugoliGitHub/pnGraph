package graphs.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Class to test the Vector
 */
public class VectorTest {

  Vector vector;

  @Test
  void createVectorWithNoLengthFails() {
    try {
      vector = new Vector(0);
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), "The size must be at least 1.");
    }
  }

  @Test
  void createVectorWithNegativeLengthFails() {
    try {
      vector = new Vector(-1);
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), "The size must be at least 1.");
    }
  }

  @Test
  void createVectorWithNegativeValueFails() {
    try {
      vector = new Vector(1, -2);
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), "The value must be positive or -1 as omega.");
    }
  }

  @Test
  void createVectorWithOneValueWorks() {
    vector = new Vector(2, 0);

    assertEquals(vector.getLength(), 2);
    assertEquals(vector.get(0), 0);
    assertEquals(vector.get(1), 0);
  }

  @Test
  void createNullVectorWorks() {
    vector = new Vector(1);

    assertEquals(vector.getLength(), 1);
    assertEquals(vector.get(0), 0);
  }

  @Test
  void createVectorWithArrayNegativeValuesFails() {
    try {
      vector = new Vector(new int[]{0, 1, -1, -2});
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), "All values must be positive or -1 as omega.");
    }
  }

  @Test
  void createVectorWithArrayWorks() {
    vector = new Vector(new int[]{0, 1, -1, 2});

    assertEquals(vector.getLength(), 4);
    assertEquals(vector.get(0), 0);
    assertEquals(vector.get(1), 1);
    assertEquals(vector.get(2), -1);
    assertEquals(vector.get(3), 2);
  }

  //other constructors

  @Test
  void containsOmegaWorks() {
    Vector vectorWithoutOmega = new Vector(new int[]{1, 2, 3});
    Vector vector = new Vector(new int[]{1, 2, -1});

    assertFalse(vectorWithoutOmega.containsOmega());
    assertTrue(vector.containsOmega());
  }

  @Test
  void lessThanWorks() {

  }

  @Test
  void lessEqualsWorks() {

  }

  //TODO: sub and add methods only changing new vector not the old
}
