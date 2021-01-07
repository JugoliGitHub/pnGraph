package main.graph;

import java.util.List;

public abstract class Node {

  protected String label;
  protected Vector input, output;

  protected Node(String label) {
    this.label = label;
    this.input = new Vector(0);
    this.output = new Vector(0);
  }

  protected Node(String label, int size) {
    this.label = label;
    this.input = new Vector(size);
    this.output = new Vector(size);
  }

  public void setOutput(Vector output) {
    this.output = output;
  }

  public void setInput(Vector input) {
    this.input = input;
  }

  /**
   * TODO: add description
   *
   * @param flow
   * @param otherNodes
   * @param dimension
   */
  public abstract void setVectors(List<Edge> flow, List<? extends Node> otherNodes, int dimension);

  public Vector getInput() { return input; }

  public Vector getOutput() { return output; }

  public int indexIn(List<? extends Node> list) {
    for(int i = 0; i < list.size(); i++) {
      if (list.get(i).equals(this)) return i;
    }
    return -1;
  }

  @Override
  public String toString() {
    return label;
  }

  public boolean equals(Node node) { return label.equals(node.label); }

}
