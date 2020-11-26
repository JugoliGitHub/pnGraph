package graph;

public class Place extends Node {

  // -1: not bounded; 0: undef; k: k-bounded
  private int boundedness;

  public Place(String label) {
    super(label);
    this.boundedness = 0;
  }

  public void incrementBoundesness() {
    if(boundedness > -1) boundedness++;
  }

  public void setBoundedness(int k) {
    if(k >= -1) this.boundedness = k;
    else throw new IllegalArgumentException("A place cannot be negative bounded.");
  }

  public int getBoundedness() { return boundedness; }

}
