package graphs.objects.nodes;

import graphs.objects.Marking;
import graphs.objects.edges.Edge;

import java.util.List;

public class Transition extends Node {

  // -1: dead; 0: not dead; 1: weak liveness; 2: alive
  int liveness;

  //TODO: labels in transition

  /**
   * Constructor of a transition.
   *
   * @param label label of the transition
   */
  public Transition(String label) {
    super(label);
    liveness = -1;
  }

  @Override
  public void setVectors(List<Edge> flow, List<? extends Node> places, int dimension) {
    int[] arrIn = new int[dimension];
    int[] arrOut = new int[dimension];
    flow.forEach(edge -> {
      Node from = edge.getFrom();
      Node to = edge.getTo();
      if (this.equals(to) && from.getClass() == Place.class) {
        arrIn[from.indexIn(places)] += 1;
      } else if (this.equals(from) && to.getClass() == Place.class) {
        arrOut[to.indexIn(places)] += 1;
      }
    });
    this.setPreSet(new Marking(arrIn));
    this.setPostSet(new Marking(arrOut));
  }

  /**
   * Returns the liveness.
   *
   * @return -1: dead; 0: not dead; 1: weak liveness; 2: alive
   */
  public int getLiveness() {
    return liveness;
  }

  /**
   * Can upgrade the liveness.
   *
   * @param liveness -1: dead; 0: not dead; 1: weak liveness; 2: alive
   */
  public void setLiveness(int liveness) {
    if (liveness >= -1 && liveness <= 2) {
      if (this.liveness < liveness) {
        this.liveness = liveness;
      }
    } else {
      throw new IllegalArgumentException("The liveness is identified by numbers between -1 and 2.");
    }
  }

  public boolean isDead() {
    return liveness == -1;
  }

  public boolean isNotDead() {
    return liveness >= 0;
  }

  public boolean isWeakAlive() {
    return liveness >= 1;
  }

  public boolean isAlive() {
    return liveness == 2;
  }

}
