package graphs.coverabilitygraphs;

import exception.WrongDimensionException;
import graphs.objects.Marking;
import graphs.objects.edges.CoverabilityGraphEdge;
import graphs.objects.nodes.Transition;
import graphs.petrinet.Petrinet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class ReachabilityGraph extends GraphForCovAndReach {

  protected int depth;

  /**
   * Constructor of a coverability-graph.
   *
   * @param mue0     the start marking
   * @param name     the name of this graph
   * @param petrinet corresponding petrinet
   */
  public ReachabilityGraph(Marking mue0, String name, Petrinet petrinet, int depth) {
    super(mue0, name, petrinet);

    this.depth = depth;
    ArrayList<Marking> path = new ArrayList<>();
    path.add(mue0);

    run(mue0, path, 0);
  }

  @Override
  protected Optional<Marking> fire(Marking mue, Transition transition)
      throws WrongDimensionException {
    if (transition.getPreSet().lessEquals(mue)) {
      Marking newMue = (Marking) mue.sub(transition.getPreSet()).add(transition.getPostSet());
      if (transition.isDead()) {
        transition.updateLiveness(0);
      }
      return Optional.of(newMue);
    } else {
      return Optional.empty();
    }
  }

  @Override
  protected void run(Marking mue, List<Marking> path, int depth) throws WrongDimensionException {
    if (depth < this.depth) {
      for (Transition transition : petrinet.getTransitions()) {
        fire(mue, transition).ifPresent(newMue -> {
          IntStream.range(0, newMue.getDimension())
              .forEach(i -> petrinet.getPlaces().get(i).setBoundednessIfHigher(newMue.get(i)));
          addToMarkings(newMue);
          addToKnots(new CoverabilityGraphEdge(mue, transition, newMue));
          if (addToVisited(newMue)) {
            path.add(newMue);
            run(newMue, path, depth + 1);
            path.remove(path.size() - 1);
          }
        });
      }
    }
  }

  @Override
  protected void run(Marking mue, List<Marking> path) {
    return;
  }

  @Override
  public String toString() {
    StringBuilder out = new StringBuilder(
        "digraph " + name + "{\n  rankdir=\"LR\";\n  node[shape=plaintext];\n");
    knots.forEach(edge -> out.append(edge.toString()));
    out.append("}\n");
    return out.append("\n").toString();
  }
}
