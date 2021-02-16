package graphs.coverabilitygraphs;

import exception.WrongDimensionException;
import graphs.objects.Marking;
import graphs.objects.edges.CoverabilityGraphEdge;
import graphs.objects.nodes.Transition;
import graphs.petrinet.Petrinet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class GraphForCovAndReach {

  protected Petrinet petrinet;

  protected String name;
  protected Marking mue0;
  protected List<Marking> markings;
  protected List<CoverabilityGraphEdge> knots;

  protected List<Marking> visited;

  public GraphForCovAndReach(Marking mue0, String name, Petrinet petrinet) {
    this.name = name;
    this.mue0 = mue0;
    this.markings = new ArrayList<>();
    this.knots = new ArrayList<>();
    markings.add(this.mue0);

    this.visited = new ArrayList<>();
    this.petrinet = petrinet;
  }

  /**
   * Adds a vector to the list markings, when it is not already present.
   *
   * @param mark marking that will be added if not present.
   * @return false when present
   */
  protected boolean addToMarkings(Marking mark) {
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
  protected boolean addToKnots(CoverabilityGraphEdge edge) {
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
  protected boolean addToVisited(Marking newVector) {
    if (visited.stream().noneMatch(newVector::equals)) {
      return visited.add(newVector);
    }
    return false;
  }

  /**
   * Fires a transition with a given mue, when possible.
   *
   * @param mue        current state of markings
   * @param transition transition to be fired
   * @return An Optional which is empty, when the transition could not be fired
   * @throws WrongDimensionException when the vector has a different dimension
   */
  protected abstract Optional<Marking> fire(Marking mue, Transition transition);

  /**
   * Implementation of 'laufe' from Algorithm 1: uebGraph. Checks every transition for a given
   * marking. If the transition can fire, it checks for omegas, adds a new edge to the graph and
   * calls itself with the new marking and the path.
   *
   * @param mue  given state of markings
   * @param path last markings to reach mue
   * @throws WrongDimensionException when the vector has a different dimension
   */
  protected abstract void run(Marking mue, List<Marking> path, int depth);

  /**
   * Implementation of 'laufe' from Algorithm 1: uebGraph. Checks every transition for a given
   * marking. If the transition can fire, it checks for omegas, adds a new edge to the graph and
   * calls itself with the new marking and the path.
   *
   * @param mue  given state of markings
   * @param path last markings to reach mue
   * @throws WrongDimensionException when the vector has a different dimension
   */
  protected abstract void run(Marking mue, List<Marking> path);
}
