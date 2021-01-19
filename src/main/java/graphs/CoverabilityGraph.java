package graphs;

import exception.WrongDimensionException;
import graphs.objects.Marking;
import graphs.objects.edges.CoverabilityGraphEdge;
import graphs.objects.edges.Edge;
import graphs.objects.nodes.Place;
import graphs.objects.nodes.Transition;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    go(mue0, path);
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
  protected Optional<Marking> fire(Marking mue, Transition transition)
      throws WrongDimensionException {
    Marking newMue = new Marking(mue.getLength());
    newMue = newMue.add(mue);
    if (transition.getPostSet().getLength() == 0 || transition.getPreSet().getLength() == 0) {
      if (petrinet.getTransitions().contains(transition)) {
        List<Place> frontPlaces = new ArrayList<>();
        List<Place> endPlaces = new ArrayList<>();

        for (Edge edge : petrinet.getFlow()) {
          if (edge.getFrom().equals(transition) && edge.getTo() instanceof Place) {
            endPlaces.add((Place) edge.getTo());
          } else if (edge.getTo().equals(transition) && edge.getFrom() instanceof Place) {
            frontPlaces.add((Place) edge.getFrom());
          }
        }

        for (Place place : frontPlaces) {
          int index = petrinet.getPlaces().indexOf(place);
          int newValueOfPlace = newMue.get(index);
          if (newValueOfPlace == 0) {
            return Optional.empty();
          } else if (newValueOfPlace >= 1) {
            newMue = newMue.subAtIndex(index, 1);
            if (newMue.getLength() == 0) {
              return Optional.empty();
            }
          }
        }
        for (Place place : endPlaces) {
          int index = petrinet.getPlaces().indexOf(place);
          int newValueOfPlace = newMue.get(index);
          newMue = newMue.addAtIndex(index, 1);
          if (place.getBoundedness() < newValueOfPlace) {
            place.setBoundedness(newValueOfPlace);
          }
        }
      }
    } else if (!newMue.sub(transition.getPreSet()).equals(new Marking(0))) {
      newMue = newMue.sub(transition.getPreSet()).add(transition.getPostSet());
      setBoundednessOfPlaces(mue, newMue);
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
  protected void go(Marking mue, List<Marking> path) throws WrongDimensionException {
    for (Transition transition : petrinet.getTransitions()) {
      Optional<Marking> newMueOptional = fire(mue, transition);
      if (newMueOptional.isPresent()) {
        Marking newMue = newMueOptional.get();
        setOmega(newMue, path);
        if (newMue.containsOmega()) {
          transition.setLiveness(1);
        }
        addToMarkings(newMue);
        addToKnots(new CoverabilityGraphEdge(mue, transition, newMue));
        if (addToVisited(newMue)) {
          path.add(newMue);
          go(newMue, path);
          path.remove(path.size() - 1);
        }
      }
    }
  }

  private static void setOmega(Marking mue, List<Marking> path) {
    path.stream()
        .filter(waypoint -> waypoint.lessThan(mue))
        .forEach(mue::setOmegas);
  }

  /**
   * Implementation of 'setzeOmega' from Algorithm 1: uebGraph.
   *
   * @param mue  state of given markings
   * @param path path
   */
  private Marking putOmega(Marking mue, List<Marking> path) {
    List<Place> places = petrinet.getPlaces();
    boolean[] omegas = new boolean[petrinet.getPlaces().size()];
    boolean[] omegaKand = new boolean[petrinet.getPlaces().size()];
    for (int i = path.size() - 1; i >= 0; i--) {
      Marking knot = path.get(i);
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
        mue = mue.setOmega(s);
        places.get(s).setBoundedness(-1);
      }
    }
    return mue;
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

  protected void setBoundednessOfPlaces(Marking mue, Marking newMue) {
    if (!newMue.equals(mue)) {
      for (int i = 0; i < newMue.getLength(); i++) {
        if (petrinet.getPlaces().get(i).getBoundedness() == -1 || newMue.get(i) == -1) {
          petrinet.getPlaces().get(i).setBoundedness(-1);
        } else if (petrinet.getPlaces().get(i).getBoundedness() < newMue.get(i)) {
          petrinet.getPlaces().get(i).setBoundedness(newMue.get(i));
        }
      }
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
