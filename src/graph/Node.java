package graph;

import exception.WrongDimensionException;

public class Node {

  protected String label;
  protected Vector preVector, postVector;

  protected Node(String label) {
    this.label = label;
  }

  protected Node(String label, int size){
    this.label = label;
    this.preVector = new Vector(0);
    this.postVector = new Vector(0);
  }

  public void setPostVector(Vector postVector) {
    this.postVector = postVector;
  }

  public void setPreVector(Vector preVector) {
    this.preVector = preVector;
  }

  @Override
  public String toString() {
    return label;
  }

  public boolean equals(Node node) {
    return label.equals(node.label);
  }
}
