import graph.CoverabilityGraphEdge;
import graph.Transition;
import graph.Vector;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CoverabilityGraph {

  private String name;
  private Vector mue0;
  private List<Vector> markings;
  private List<CoverabilityGraphEdge> knots;

  private List<Vector> visited;
  private Petrinet petrinet;

  public CoverabilityGraph(Vector mue0, String name, Petrinet petrinet) {
    this.name = name;
    this.mue0 = mue0;
    this.markings = new ArrayList<>();
    this.knots = new ArrayList<>();
    markings.add(this.mue0);

    this.visited = new ArrayList<>();
    this.petrinet = petrinet;
    go(mue0, List.of(mue0));
  }

  public boolean addToMarkings(Vector mark) {
    return markings.contains(mark) ? markings.add(mark) : false;
  }

  public boolean addToKnots(CoverabilityGraphEdge edge) {
    return knots.contains(edge) ? knots.add(edge) : false;
  }

  private boolean addToVisited(Vector newVector) {
    return visited.contains(newVector) ? visited.add(newVector) : false;
  }

  private Optional<Vector> fire(Vector mue, Transition transition) {
    return Optional.empty();
  }

  private void go(Vector mue, List<Vector> path) {
    for(Transition transition : petrinet.getTransitions()) {
      this.fire(mue, transition).ifPresent(newMue -> {
        setOmega(newMue, path);
        addToMarkings(newMue);
        addToKnots(new CoverabilityGraphEdge(mue, transition, newMue));
        if (addToVisited(newMue)) {
          path.add(newMue);
          go(newMue, path);
          path.remove(path.size() - 1);
        }
      });
    }
  }

  private void setOmega(Vector mue, List<Vector> path) {

  }

  @Override
  public String toString() {
    StringBuilder out = new StringBuilder(
        "digraph " + name + "{\nrankdir=\"LR\";\nnode[shape=plaintext];\n");
    knots.forEach(edge -> out.append(edge.toString()));
    return out.toString();
  }
}
