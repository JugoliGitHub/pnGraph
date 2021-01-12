package graphs.objects.nodes;

import graphs.objects.Vector;
import graphs.objects.edges.Edge;
import java.util.List;

/**
 * Class for a place of a petrinet.
 */
public class Place extends Node {

  private int capacity;
  // -1: not bounded; 0: undef; k: k-bounded
  private int boundedness;

  /**
   * Constructor to create a place with a specific label.
   *
   * @param label string to label the place
   */
  public Place(String label) {
    super(label);
    this.boundedness = 0;
    this.capacity = -1;
  }

  @Override
  public void setVectors(List<Edge> flow, List<? extends Node> transitions, int dimension) {
    int[] arrIn = new int[dimension];
    int[] arrOut = new int[dimension];
    flow.forEach(edge -> {
      Node from = edge.getFrom();
      Node to = edge.getTo();
      if (this.equals(to) && from.getClass() == Transition.class) {
        arrIn[from.indexIn(transitions)] += 1;
      } else if (this.equals(from) && to.getClass() == Transition.class) {
        arrOut[to.indexIn(transitions)] += 1;
      }
    });
    this.setInput(new Vector(arrIn));
    this.setOutput(new Vector(arrOut));
  }

  /**
   * Increments the boundedness of this place.
   */
  public void incrementBoundedness() {
    if (boundedness > -1) {
      boundedness++;
    }
  }

  public int getBoundedness() {
    return boundedness;
  }

  /**
   * Sets the boundedness to a value k.
   *
   * @param k an integer bigger equal 0 or -1 if the place is unbounded
   */
  public void setBoundedness(int k) {
    if (k >= -1) {
      this.boundedness = k;
    } else {
      throw new IllegalArgumentException("A place cannot be negative bounded.");
    }
  }

  public int getCapacity() {
    return this.capacity;
  }

  /**
   * Sets the capacity to a value c.
   *
   * @param c an integer bigger equal 0 or -1 if the place has no capacity
   */
  public void setCapacity(int c) {
    if (c >= -1) {
      this.capacity = c;
    } else {
      throw new IllegalArgumentException("A place cannot have a negative capacity.");
    }
  }

}
