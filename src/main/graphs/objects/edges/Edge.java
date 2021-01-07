package main.graphs.objects.edges;

import main.graphs.objects.nodes.Node;

public class Edge<K extends Node, V extends Node> {

  private final K from;
  private final V to;

  public Edge(K from, V to) {
    this.from = from;
    this.to = to;
  }

  public K getFrom() {
    return from;
  }

  public V getTo() {
    return to;
  }

  public Edge<V,K> reverse() {
    return new Edge<>(this.getTo(), this.getFrom());
  }

  @Override
  public String toString() {
    return "  \"" + from.toString() + "\" -> \"" + to.toString() + "\";\n";
  }

}
