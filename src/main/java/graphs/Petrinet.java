package graphs;

import graphs.objects.Vector;
import graphs.objects.edges.Edge;
import graphs.objects.nodes.Node;
import graphs.objects.nodes.Place;
import graphs.objects.nodes.Transition;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Petrinet {

  protected final String name;
  protected final List<Place> places;
  protected final List<Transition> transitions;
  protected final List<Edge> flow;
  protected final Vector mue0;

  /**
   * Creates an empty petrinet. Should initialize mue_0, capacity (if present) and places,
   * transitions and flow.
   *
   * @param name name of the net
   */
  public Petrinet(String name, List<Place> places, List<Transition> transitions, List<Edge> flow,
      Vector mue0) {
    this.name = name;
    this.places = places;
    this.transitions = transitions;
    this.flow = flow;
    this.mue0 = mue0;
    if (!checkCorrectness()) {
      throw new IllegalArgumentException("This is no valid petrinet");
    }
    setVectors();
    setInitialBoundedness();
  }

  public Vector getMue0() {
    return this.mue0;
  }

  //TODO  return new Petrinet with an extra place
  //TODO: change vector mue 0
  public void addPlace(Place place) {
    places.add(place);
  }

  public List<Place> getPlaces() {
    return places;
  }

  public void addTransition(Transition transition) {
    this.transitions.add(transition);
  }

  public List<Transition> getTransitions() {
    return transitions;
  }

  public void addEdge(Edge<? extends Node, ? extends Node> edge) {
    flow.add(edge);
  }

  public List<Edge> getFlow() {
    return flow;
  }

  public Vector getParekhVector(Place s) {
    return new Vector(places.size());
  }

  /**
   * Sets the start- and end-nodes for every node.
   */
  private void setVectors() {
    new Thread(() ->
        places.forEach(place -> place.setVectors(flow, transitions, transitions.size()))).start();
    new Thread(() ->
        transitions.forEach(transition -> transition.setVectors(flow, places, places.size())))
        .start();
  }

  /**
   * Initializes every node with the boundedness of the initial marking mue_0.
   */
  protected void setInitialBoundedness() {
    for (int i = 0; i < places.size(); i++) {
      places.get(i).setBoundedness(mue0.get(i));
    }
  }

  protected boolean checkCorrectness() {
    // TODO: check correctness
    // throw exceptions and kill user D:
    return isSameLength();
  }

  private boolean isSameLength() {
    return mue0.getLength() == places.size();
  }

  public boolean containsLoop() {
    //TODO: implement
    return false;
  }

  /**
   * Returns the Liveness of the petri-net.
   *
   * @return -1: dead; 0: not dead; 1: weak liveness; 2: alive
   */
  public int getLiveness() {
    return Collections
        .min(transitions.stream().map(Transition::getLiveness).collect(Collectors.toList()));
  }

  //TODO: structural liveness and deadlock-free

  /**
   * Returns the maximal value of a bounded place.
   *
   * @return -1 for unbounded (omega), k as the maximal bound
   */
  public int getBoundedness() {
    return places.stream().map(Place::getBoundedness).filter(bound -> bound == -1)
        .findAny()
        .orElse(Collections
            .max(places.stream().map(Place::getBoundedness).collect(Collectors.toList())));
  }

  /**
   * Returns the reversed net. That means that the flow changes its direction.
   *
   * @return a reversed petrinet
   */
  public Petrinet reversed() {
    return new Petrinet(this.name + "Reversed", List.copyOf(places),
        List.copyOf(transitions), flow.stream().map(Edge::reverse).collect(Collectors.toList()),
        mue0.copy());
  }

  /**
   * Creates the dual-graph to this petrinet. This means every place becomes a transition and vice
   * versa.
   *
   * @return a new petrinet but without markings
   */
  public Petrinet dual() {
    List<Place> newPlaces = reverseTransitionsToPlaces();
    List<Transition> newTransitions = reversePlacesToTransitions();
    return new Petrinet(this.name + "Dual", newPlaces, newTransitions, List.copyOf(flow),
        new Vector(newPlaces.size()));
  }

  /**
   * Return a dual-petrinet with markings.
   *
   * @param markings new combination of markings for new places
   * @return a new petrinet but with markings
   */
  public Petrinet dual(Vector markings) {
    List<Place> newPlaces = reverseTransitionsToPlaces();
    List<Transition> newTransitions = reversePlacesToTransitions();
    return new Petrinet(this.name + "Dual", newPlaces, newTransitions, List.copyOf(flow),
        markings);
  }

  private List<Place> reverseTransitionsToPlaces() {
    return transitions.stream().map(transition -> new Place(transition.getLabel()))
        .collect(Collectors.toList());
  }

  private List<Transition> reversePlacesToTransitions() {
    return places.stream().map(place -> new Transition(place.getLabel()))
        .collect(Collectors.toList());
  }

  @Override
  public String toString() {
    StringBuilder out = new StringBuilder(String
        .format("digraph %s{\nnode[shape=circle];\n", name));
    for (int i = 0; i < places.size(); i++) {
      if (mue0.get(i) == 0) {
        out.append("  \"").append(places.get(i).toString()).append("\";\n");
      } else {
        StringBuilder label = new StringBuilder();
        if (mue0.get(i) <= 6) {
          for (int j = 0; j < i; j++) {
            label.append("&bull;");
          }
        } else {
          label.append(mue0.get(i));
        }
        out.append("  \"").append(places.get(i).toString()).append("\" [label=\"").append(label)
            .append("\"];\n");
      }
    }
    transitions.forEach(
        transition -> out.append("  \"").append(transition.toString()).append("\" [shape=box];\n"));
    flow.forEach(edge -> out.append(edge.toString()));
    out.append("}");
    return out.append("\n").toString();
  }

  /**
   * Checks whether two petrinets are same or not.
   *
   * @param petrinet other petrinet
   * @return boolean if true
   */
  public boolean equals(Petrinet petrinet) {
    //TODO: implement
    // two nets can be equal beside their place/transition names
    return false;
  }

}
