package graphs.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Class to test the Vector
 */
public class MarkingTest {

  Marking marking;

  @Test
  void createVectorWithNoLengthFails() {
    try {
      marking = new Marking(0);
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), "The size must be at least 1.");
    }
  }

  @Test
  void createVectorWithNegativeLengthFails() {
    try {
      marking = new Marking(-1);
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), "The size must be at least 1.");
    }
  }

  @Test
  void createVectorWithNegativeValueFails() {
    try {
      marking = new Marking(1, -2);
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), "The value must be positive or -1 as omega.");
    }
  }

  @Test
  void createVectorWithOneValueWorks() {
    marking = new Marking(2, 0);

    assertEquals(marking.getLength(), 2);
    assertEquals(marking.get(0), 0);
    assertEquals(marking.get(1), 0);
  }

  @Test
  void createNullVectorWorks() {
    marking = new Marking(1);

    assertEquals(marking.getLength(), 1);
    assertEquals(marking.get(0), 0);
  }

  @Test
  void createVectorWithArrayNegativeValuesFails() {
    try {
      marking = new Marking(new int[]{0, 1, -1, -2});
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), "All values must be positive or -1 as omega.");
    }
  }

  @Test
  void createVectorWithArrayWorks() {
    marking = new Marking(new int[]{0, 1, -1, 2});

    assertEquals(marking.getLength(), 4);
    assertEquals(marking.get(0), 0);
    assertEquals(marking.get(1), 1);
    assertEquals(marking.get(2), -1);
    assertEquals(marking.get(3), 2);
  }

  //other constructors

  @Test
  void containsOmegaWorks() {
    Marking markingWithoutOmega = new Marking(new int[]{1, 2, 3});
    Marking marking = new Marking(new int[]{1, 2, -1});

    assertFalse(markingWithoutOmega.containsOmega());
    assertTrue(marking.containsOmega());
  }

  @Test
  void lessThanWorks() {

  }

  @Test
  void lessEqualsWorks() {

  }

  //TODO: sub and add methods only changing new vector not the old
}
