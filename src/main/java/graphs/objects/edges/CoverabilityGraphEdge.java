package graphs.objects.edges;

import graphs.objects.Marking;
import graphs.objects.nodes.Transition;

/**
 * An edge of a coverability graph for petrinets. Has markings as nodes which it connects.
 */
public class CoverabilityGraphEdge {

  private final Marking from;
  private final Marking to;
  private final Transition transition;

  /**
   * Creates a new edge of a coverability-graph.
   *
   * @param from       the from marking
   * @param transition the used transition
   * @param to         the to marking
   */
  public CoverabilityGraphEdge(Marking from, Transition transition, Marking to) {
    this.from = from;
    this.to = to;
    this.transition = transition;
  }

  public Transition getTransition() {
    return transition;
  }

  public Marking getFrom() {
    return from;
  }

  public Marking getTo() {
    return to;
  }

  @Override
  public String toString() {
    return "  \"" + from.toString() + "\" -> \"" + to.toString() + "\" [label=\"" + transition
        + "\"];\n";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof CoverabilityGraphEdge)) {
      return false;
    }
    CoverabilityGraphEdge that = (CoverabilityGraphEdge) obj;
    return from.equals(that.from)
        && to.equals(that.to)
        && transition.equals(that.transition);
  }
}
