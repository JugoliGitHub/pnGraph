package graphs.objects.nodes;

import graphs.objects.Marking;
import graphs.objects.Vector;
import graphs.objects.edges.Edge;
import java.util.List;

/**
 * A node class for nodes of a petrinet.
 */
public abstract class Node {

  protected final String label;
  protected Marking preSet;
  protected Marking postSet;

  protected Node(String label) {
    this.label = label;
    this.preSet = new Marking(0);
    this.postSet = new Marking(0);
  }

  protected Node(String label, int size) {
    this.label = label;
    this.preSet = new Marking(size);
    this.postSet = new Marking(size);
  }

  /**
   * Sets the input and output vectors to a corresponding petrinet.
   *
   * @param flow       flow of the petrinet
   * @param otherNodes nodes of other type from the petrinet
   * @param dimension  length of the other nodes
   */
  public abstract void setVectors(List<Edge> flow, List<? extends Node> otherNodes, int dimension);

  public Vector getPreSet() {
    return preSet;
  }

  protected void setPreSet(Marking preSet) {
    this.preSet = preSet;
  }

  public Vector getPostSet() {
    return postSet;
  }

  protected void setPostSet(Marking postSet) {
    this.postSet = postSet;
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
