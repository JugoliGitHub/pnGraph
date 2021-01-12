package graphs.objects.nodes;

import graphs.objects.Vector;
import graphs.objects.edges.Edge;
import java.util.List;

public abstract class Node {

  protected String label;
  protected Vector input;
  protected Vector output;

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

  /**
   * Sets the input and output vectors.
   *
   * @param flow       flow of the petrinet
   * @param otherNodes nodes of other type from the petrinet
   * @param dimension  length of the other nodes
   */
  public abstract void setVectors(List<Edge> flow, List<? extends Node> otherNodes, int dimension);

  public Vector getInput() {
    return input;
  }

  public void setInput(Vector input) {
    this.input = input;
  }

  public Vector getOutput() {
    return output;
  }

  public void setOutput(Vector output) {
    this.output = output;
  }

  public String getLabel() {
    return this.label;
  }

  /**
   * Returns the index of this node in a list of implemented nodes.
   *
   * @param list the list of nodes of this type
   * @return the index or -1 if not in list
   */
  public int indexIn(List<? extends Node> list) {
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).equals(this)) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public String toString() {
    return label;
  }

  public boolean equals(Node node) {
    return label.equals(node.label);
  }

}
