package graph;

public class Place extends Node {

  // -1: not bounded; 0: undef; k: k-bounded
  private int boundedness;
  private int capacity;

  public Place(String label) {
    super(label);
    this.boundedness = 0;
    this.capacity = -1;
  }

  public Place(String label, int capacity) {
    super(label);
    this.boundedness = 0;
    this.capacity = capacity;
  }

  public void incrementBoundedness() {
    if (boundedness > -1) {
      boundedness++;
    }
  }

  public void setBoundedness(int k) {
    if (k >= -1) {
      this.boundedness = k;
    } else {
      throw new IllegalArgumentException("A place cannot be negative bounded.");
    }
  }

  public int getBoundedness() { return boundedness; }

  public void setCapacity(int c) {
    if (c >= -1) {
      this.boundedness = c;
    } else {
      throw new IllegalArgumentException("A place cannot have a negative bounding.");
    }
  }

  public int getCapacity() {return this.capacity; }

}
