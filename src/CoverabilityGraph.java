import exception.WrongDimensionException;
import graph.CoverabilityGraphEdge;
import graph.Edge;
import graph.Place;
import graph.Transition;
import graph.Vector;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A class to create coverability-graphs for a petri-net
 */
public class CoverabilityGraph {

  private Petrinet petrinet;

  private String name;
  private Vector mue0;
  private List<Vector> markings;
  private List<CoverabilityGraphEdge> knots;

  private List<Vector> visited;

  /**
   * Constructor
   */
  public CoverabilityGraph(Vector mue0, String name, Petrinet petrinet)
      throws WrongDimensionException {
    this.name = name;
    this.mue0 = mue0;
    this.markings = new ArrayList<>();
    this.knots = new ArrayList<>();
    markings.add(this.mue0);

    this.visited = new ArrayList<>();
    this.petrinet = petrinet;
    ArrayList<Vector> path = new ArrayList<>();
    path.add(mue0);
    go(mue0, path);
    petrinet.getTransitions().forEach(this::setLiveness);
  }

  /**
   * Adds a vector to the list markings, when it is not already present.
   *
   * @param mark marking that will be added if not present.
   * @return false when present
   */
  public boolean addToMarkings(Vector mark) {
    Optional<Vector> opt = markings.stream().filter(elem -> elem.equals(mark)).findFirst();
    return opt.isPresent() ? false : markings.add(mark);
  }

  /**
   * Adds a edge to the list knots, when it is not already present.
   *
   * @param edge edge that will be added if not present
   * @return false when present
   */
  public boolean addToKnots(CoverabilityGraphEdge edge) {
    Optional<CoverabilityGraphEdge> opt = knots.stream().filter(elem -> elem.equals(edge))
        .findFirst();
    return opt.isPresent() ? false : knots.add(edge);
  }

  /**
   * Adds a vector to the list of visited mues, when it is not already visited.
   *
   * @param newVector the new mue
   * @return false when visited
   */
  private boolean addToVisited(Vector newVector) {
    Optional<Vector> opt = visited.stream().filter(elem -> elem.equals(newVector)).findFirst();
    return opt.isPresent() ? false : visited.add(newVector);
  }

  /**
   * Fires a transition with a given mue, when possible. This method will create own front and end
   * places if necessary. When the transition has them it will calculate the following state with
   * them.
   *
   * @param mue        current state of markings
   * @param transition transition to be fired
   * @return An Optional which is empty, when the transition could not be fired
   * @throws WrongDimensionException when the vector has a different dimension
   */
  private Optional<Vector> fire(Vector mue, Transition transition) throws WrongDimensionException {
    Vector newMue = new Vector(mue.getLength());
    newMue.add(mue);
    if (transition.getOutput().getLength() == 0 || transition.getInput().getLength() == 0) {
      if (petrinet.getTransitions().contains(transition)) {
        List<Place> front_places = new ArrayList<>();
        List<Place> end_places = new ArrayList<>();

        for (Edge edge : petrinet.getFlow()) {
          if (edge.getFrom().equals(transition) && edge.getTo() instanceof Place) {
            end_places.add((Place) edge.getTo());
          } else if (edge.getTo().equals(transition) && edge.getFrom() instanceof Place) {
            front_places.add((Place) edge.getFrom());
          }
        }

        for (Place place : front_places) {
          int index = petrinet.getPlaces().indexOf(place);
          int newValueOfPlace = newMue.get(index);
          if (newValueOfPlace == 0) {
            return Optional.empty();
          } else if (newValueOfPlace >= 1) {
            if (!newMue.subAtIndex(index, 1)) {
              return Optional.empty();
            }
          }
        }
        for (Place place : end_places) {
          int index = petrinet.getPlaces().indexOf(place);
          int newValueOfPlace = newMue.get(index);
          newMue.addAtIndex(index, 1);
          if (place.getBoundedness() < newValueOfPlace) {
            place.setBoundedness(newValueOfPlace);
          }
        }
      }
    }
    else if (newMue.sub(transition.getInput())) {
      newMue.add(transition.getOutput());
    } else {
      return Optional.empty();
    }
    if (transition.isDead()) {
      transition.setLiveness(0);
    }
    return Optional.of(newMue);
  }

  /**
   * Implementation of 'laufe' from Algorithm 1: uebGraph.
   *
   * @param mue  given state of markings
   * @param path path
   * @throws WrongDimensionException when the vector has a different dimension
   */
  private void go(Vector mue, List<Vector> path) throws WrongDimensionException {
    for (Transition transition : petrinet.getTransitions()) {
      Optional<Vector> newMueOptional = fire(mue, transition);
      if (newMueOptional.isPresent()) {
        Vector newMue = newMueOptional.get();
        newMue = setOmega(newMue, path);
        if (newMue.containsOmega()) {
          transition.setLiveness(1);
        }
        addToMarkings(newMue);
        System.out.println(markings);
        addToKnots(new CoverabilityGraphEdge(mue, transition, newMue));
        if (addToVisited(newMue)) {
          path.add(newMue);
          go(newMue, path);
          path.remove(path.size() - 1);
        }
      }
    }
  }

  /**
   * Implementation of 'setzeOmega' from Algorithm 1: uebGraph
   *
   * @param mue  state of given markings
   * @param path path
   */
  private Vector setOmega(Vector mue, List<Vector> path) {
    List<Place> places = petrinet.getPlaces();
    boolean[] omegas = new boolean[petrinet.getPlaces().size()];
    boolean[] omegaKand = new boolean[petrinet.getPlaces().size()];
    for (int i = path.size() - 1; i >= 0; i--) {
      Vector knot = path.get(i);
      if (knot.lessThan(mue)) {
        for (int s = 0; s < places.size(); s++) {
          if (knot.get(s) < mue.get(s) && mue.get(s) != -1) {
            omegaKand[s] = true;
          }
        }
      }
      for (int s = 0; s < places.size(); s++) {
        if (omegaKand[s]) {
          omegas[s] = true;
        }
      }
      omegaKand = new boolean[petrinet.getPlaces().size()];
    }
    for (int s = 0; s < places.size(); s++) {
      if (omegas[s]) {
        mue.setOmega(s);
        places.get(s).setBoundedness(-1);
      }
    }
    return mue;
  }

  private void setLiveness(Transition t) {
    //TODO: when is alive?
    if (false) {
      t.setLiveness(2);
    } else if (knots.stream().filter(k -> k.getTransition() == t).allMatch(this::findLoop)) {
      t.setLiveness(1);
    } else if (knots.stream().anyMatch(k -> k.getTransition() == t)) {
      t.setLiveness(0);
    } else {
      t.setLiveness(-1);
    }
  }

  private boolean findLoop(CoverabilityGraphEdge edge) {
    return knots.stream().filter(edge2 -> edge2.getFrom().equals(edge.getTo()))
        .map(knot -> findLoopRecursive(edge.getFrom(), new ArrayList<>(), knot)).findFirst()
        .isPresent();
  }

  private boolean findLoopRecursive(Vector from, List<Vector> visitedVectors,
      CoverabilityGraphEdge edge) {
    Vector edgeTo = edge.getTo();
    if (edgeTo.equals(from)) {
      return true;
    } else if (visitedVectors.contains(edgeTo)) {
      return false;
    } else {
      visitedVectors.add(edgeTo);
      return knots.stream().filter(edge2 -> edge2.getFrom().equals(edgeTo))
          .map(knot -> findLoopRecursive(from, visitedVectors, knot)).findFirst().isPresent();
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
