package graphs.objects.edges;

import graphs.objects.nodes.Node;
import java.util.Objects;

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

  public Edge<V, K> reverse() {
    Edge<V, K> newEdge = new Edge<>(this.getTo(), this.getFrom());
    return newEdge;
  }

  @Override
  public String toString() {
    return "  \"" + from.toString() + "\" -> \"" + to.toString() + "\";\n";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Edge)) {
      return false;
    }

    Edge<?, ?> edge = (Edge<?, ?>) o;

    if (!Objects.equals(from, edge.from)) {
      return false;
    }
    return Objects.equals(to, edge.to);
  }

}
