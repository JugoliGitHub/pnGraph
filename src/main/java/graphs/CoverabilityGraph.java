package graphs;

import exception.WrongDimensionException;
import graphs.objects.Marking;
import graphs.objects.edges.CoverabilityGraphEdge;
import graphs.objects.nodes.Transition;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * A class to create coverability-graphs for a petrinet.
 */
public class CoverabilityGraph {

  protected Petrinet petrinet;

  protected String name;
  protected Marking mue0;
  protected List<Marking> markings;
  protected List<CoverabilityGraphEdge> knots;

  protected List<Marking> visited;

  /**
   * Constructor of a coverability-graph.
   *
   * @param mue0     the start marking
   * @param name     the name of this graph
   * @param petrinet corresponding petrinet
   */
  public CoverabilityGraph(Marking mue0, String name, Petrinet petrinet) {
    this.name = name;
    this.mue0 = mue0;
    this.markings = new ArrayList<>();
    this.knots = new ArrayList<>();
    markings.add(this.mue0);

    this.visited = new ArrayList<>();
    this.petrinet = petrinet;
    ArrayList<Marking> path = new ArrayList<>();
    path.add(mue0);
    run(mue0, path);
    petrinet.getTransitions().forEach(this::setLiveness);
  }

  protected CoverabilityGraph(Marking mue0, String name) {
    this.name = name;
    this.mue0 = mue0;
    this.markings = new ArrayList<>();
    this.knots = new ArrayList<>();
    markings.add(this.mue0);

    this.visited = new ArrayList<>();
  }

  /**
   * Adds a vector to the list markings, when it is not already present.
   *
   * @param mark marking that will be added if not present.
   * @return false when present
   */
  public boolean addToMarkings(Marking mark) {
    if (markings.stream().noneMatch(mark::equals)) {
      return markings.add(mark);
    }
    return false;
  }

  /**
   * Adds a edge to the list knots, when it is not already present.
   *
   * @param edge edge that will be added if not present
   * @return false when present
   */
  public boolean addToKnots(CoverabilityGraphEdge edge) {
    if (knots.stream().noneMatch(edge::equals)) {
      return knots.add(edge);
    }
    return false;
  }

  /**
   * Adds a vector to the list of visited mues, when it is not already visited.
   *
   * @param newVector the new mue
   * @return false when visited
   */
  private boolean addToVisited(Marking newVector) {
    if (visited.stream().noneMatch(newVector::equals)) {
      return visited.add(newVector);
    }
    return false;
  }

  protected void setBoundednessOfPlaces(Marking mue, Marking newMue) {
    if (!newMue.equals(mue)) {
      IntStream.range(0, mue.getLength()).forEach(i -> {
        if (petrinet.getPlaces().get(i).getBoundedness() == -1 || newMue.get(i) == -1) {
          petrinet.getPlaces().get(i).setBoundedness(-1);
        } else if (petrinet.getPlaces().get(i).getBoundedness() < newMue.get(i)) {
          petrinet.getPlaces().get(i).setBoundedness(newMue.get(i));
        }
      });
    }
  }

  /**
   * Fires a transition with a given mue, when possible.
   *
   * @param mue        current state of markings
   * @param transition transition to be fired
   * @return An Optional which is empty, when the transition could not be fired
   * @throws WrongDimensionException when the vector has a different dimension
   */
  protected Optional<Marking> fire(Marking mue, Transition transition)
      throws WrongDimensionException {
    if (!mue.sub(transition.getPreSet()).equals(new Marking(0))) {
      Marking newMue = mue.sub(transition.getPreSet()).add(transition.getPostSet());
      setBoundednessOfPlaces(mue, newMue);
      if (transition.isDead()) {
        transition.setLiveness(0);
      }
      return Optional.of(newMue);
    } else {
      return Optional.empty();
    }
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

  /**
   * Implementation of 'laufe' from Algorithm 1: uebGraph. Checks every transition for a given
   * marking. If the transition can fire, it checks for omegas, adds a new edge to the graph and
   * calls itself with the new marking and the path.
   *
   * @param mue  given state of markings
   * @param path last markings to reach mue
   * @throws WrongDimensionException when the vector has a different dimension
   */
  protected void run(Marking mue, List<Marking> path) throws WrongDimensionException {
    for (Transition transition : petrinet.getTransitions()) {
      fire(mue, transition).ifPresent(newMue -> {
        setOmega(newMue, path);
        if (newMue.containsOmega()) {
          transition.setLiveness(1);
        }
        IntStream.range(0, newMue.getLength())
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
    //TODO: when is alive?
    if (knots.stream().filter(k -> k.getTransition() == transition).allMatch(this::findLoop)) {
      transition.setLiveness(2);
    } else if (knots.stream().filter(k -> k.getTransition() == transition)
        .anyMatch(this::findLoop)) {
      transition.setLiveness(1);
    } else if (knots.stream().anyMatch(k -> k.getTransition() == transition)) {
      transition.setLiveness(0);
    } else {
      transition.setLiveness(-1);
    }
  }

  private boolean findLoop(CoverabilityGraphEdge edge) {
    return knots.stream().filter(edge2 -> edge2.getFrom().equals(edge.getTo()))
        .map(knot -> findLoopRecursive(edge.getFrom(), new ArrayList<>(), knot)).findFirst()
        .isPresent();
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
          .map(knot -> findLoopRecursive(from, visitedMarkings, knot)).findFirst().isPresent();
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
