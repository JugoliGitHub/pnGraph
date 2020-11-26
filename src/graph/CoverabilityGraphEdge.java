package graph;

public class CoverabilityGraphEdge {

  private Vector from, to;
  private Transition transition;

  public CoverabilityGraphEdge(Vector from, Transition transition, Vector to) {
    this.from = from;
    this.to = to;
    this.transition = transition;
  }

  @Override
  public String toString() {
    return "  \"" + from.toString() + "\" -> \"" + to.toString() + "\" [label=\"" + transition
        + "\"];\n";
  }
}
