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

/**
 * A class to create coverability-graphs for a petrinet.
 */
public class CoverabilityGraph extends GraphForCovAndReach {

  /**
   * Constructor of a coverability-graph.
   *
   * @param mue0     the start marking
   * @param name     the name of this graph
   * @param petrinet corresponding petrinet
   */
  public CoverabilityGraph(Marking mue0, String name, Petrinet petrinet) {
    super(mue0, name, petrinet);

    ArrayList<Marking> path = new ArrayList<>();
    path.add(mue0);
    run(mue0, path);
    petrinet.getTransitions().forEach(this::setLiveness);
  }

  /**
   * Changes a markings elements to omega, when markings on the path are less than the mue.
   *
   * @param mue  the current marking
   * @param path the last markings to reach mue
   */
  private static void setOmega(Marking mue, List<Marking> path) {
    path.stream()
        .filter(waypoint -> waypoint.lessThan(mue))
        .forEach(mue::setOmegas);
  }

  protected void setBoundednessOfPlaces(Marking mue, Marking newMue) {
    if (!newMue.equals(mue)) {
      IntStream.range(0, mue.getDimension()).forEach(i -> {
        if (petrinet.getPlaces().get(i).getBoundedness() == -1 || newMue.get(i) == -1) {
          petrinet.getPlaces().get(i).setBoundedness(-1);
        } else if (petrinet.getPlaces().get(i).getBoundedness() < newMue.get(i)) {
          petrinet.getPlaces().get(i).setBoundedness(newMue.get(i));
        }
      });
    }
  }

  @Override
  protected Optional<Marking> fire(Marking mue, Transition transition)
      throws WrongDimensionException {
    if (transition.getPreSet().lessEquals(mue)) {
      Marking newMue = (Marking) mue.sub(transition.getPreSet()).add(transition.getPostSet());
      setBoundednessOfPlaces(mue, newMue);
      if (transition.isDead()) {
        transition.updateLiveness(0);
      }
      return Optional.of(newMue);
    } else {
      return Optional.empty();
    }
  }

  @Override
  protected void run(Marking mue, List<Marking> path, int depth) {
    return;
  }

  @Override
  protected void run(Marking mue, List<Marking> path) throws WrongDimensionException {
    for (Transition transition : petrinet.getTransitions()) {
      fire(mue, transition).ifPresent(newMue -> {
        setOmega(newMue, path);
        IntStream.range(0, newMue.getDimension())
            .forEach(i -> petrinet.getPlaces().get(i).setBoundednessIfHigher(newMue.get(i)));
        addToMarkings(newMue);
        addToKnots(new CoverabilityGraphEdge(mue, transition, newMue));
        if (addToVisited(newMue)) {
          path.add(newMue);
          run(newMue, path);
          path.remove(path.size() - 1);
        }
      });
    }
  }

  protected void setLiveness(Transition transition) {
    //TODO: alive correct?
    if (markings.stream().allMatch(m ->
        knots.stream().anyMatch(k -> k.getFrom().equals(m) && k.getTransition() == transition))) {
      transition.updateLiveness(2);
    } else if (knots.stream().filter(k -> k.getTransition() == transition)
        .anyMatch(this::findLoop) && this.knots.size() > 0) {
      transition.updateLiveness(1);
    } else if (knots.stream().anyMatch(k -> k.getTransition() == transition)) {
      transition.updateLiveness(0);
    } else {
      transition.updateLiveness(-1);
    }
  }

  private boolean findLoop(CoverabilityGraphEdge edge) {
    return knots.stream().filter(edge2 -> edge2.getFrom().equals(edge.getTo()))
        .anyMatch(knot -> findLoopRecursive(edge.getFrom(), new ArrayList<>(), knot));
  }

  private boolean findLoopRecursive(Marking from, List<Marking> visitedMarkings,
      CoverabilityGraphEdge edge) {
    Marking edgeTo = edge.getTo();
    if (edgeTo.equals(from)) {
      return true;
    } else if (visitedMarkings.contains(edgeTo)) {
      return false;
    } else {
      visitedMarkings.add(edgeTo);
      return knots.stream().filter(edge2 -> edge2.getFrom().equals(edgeTo))
          .anyMatch(knot -> findLoopRecursive(from, visitedMarkings, knot));
    }
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
