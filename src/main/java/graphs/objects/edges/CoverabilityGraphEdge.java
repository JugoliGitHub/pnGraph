package graphs.objects.edges;

import java.util.Objects;
import graphs.objects.Vector;
import graphs.objects.nodes.Transition;

public class CoverabilityGraphEdge {

  private Vector from, to;
  private Transition transition;

  public CoverabilityGraphEdge(Vector from, Transition transition, Vector to) {
    this.from = from;
    this.to = to;
    this.transition = transition;
  }

  public Transition getTransition() {
    return transition;
  }

  public Vector getFrom() {
    return from;
  }

  public Vector getTo() {
    return to;
  }

  @Override
  public String toString() {
    return "  \"" + from.toString() + "\" -> \"" + to.toString() + "\" [label=\"" + transition
        + "\"];\n";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CoverabilityGraphEdge)) {
      return false;
    }
    CoverabilityGraphEdge that = (CoverabilityGraphEdge) o;
    return from.equals(that.from) &&
        to.equals(that.to) &&
        transition.equals(that.transition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to, transition);
  }
}