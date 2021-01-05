package graph;

public class Transition extends Node {

  // -1: dead; 0: not dead; 1: weak liveness; 2: alive
  int liveness;


  /**
   * Constructor of a transition.
   * @param label label of the transition
   */
  public Transition(String label) {
    super(label);
    liveness = 0;
  }

  /**
   * Can upgrade the liveness.
   * @param liveness -1: dead; 0: not dead; 1: weak liveness; 2: alive
   */
  public void setLiveness(int liveness) {
    if(liveness >= -1 && liveness <= 2) {
      if(this.liveness < liveness) this.liveness = liveness;
    } else {
      throw new IllegalArgumentException("The liveness is identified by numbers between -1 and 2.");
    }
  }

  /**
   * Returns the liveness.
   * @return -1: dead; 0: not dead; 1: weak liveness; 2: alive
   */
  public int getLiveness() { return liveness; }

  public boolean isDead() { return liveness == -1; }

  public boolean isNotDead() { return liveness >= 0; }

  public boolean isWeakAlive() { return liveness >= 1; }

  public boolean isAlive() { return liveness == 2; }

}
