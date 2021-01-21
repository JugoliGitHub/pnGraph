package graphs;

import graphs.objects.Marking;
import graphs.objects.Matrix;
import graphs.objects.Vector;
import graphs.objects.edges.Edge;
import graphs.objects.nodes.Node;
import graphs.objects.nodes.Place;
import graphs.objects.nodes.Transition;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Petrinet {

  protected final String name;
  protected final List<Place> places;
  protected final List<Transition> transitions;
  protected final List<Edge> flow;
  protected final Marking mue0;

  protected final Map<Place, Set<Place>> pathsFromPlace;

  protected Matrix incidenceMatrix;
  protected Matrix forwardMatrix;
  protected Matrix backwardMatrix;

  /**
   * Creates an empty petrinet. Should initialize mue0, capacity (if present) and places,
   * transitions and flow.
   *
   * @param name name of the net
   */
  public Petrinet(String name, List<Place> places, List<Transition> transitions, List<Edge> flow,
      Marking mue0) {
    this.name = name;
    this.places = places;
    this.transitions = transitions;
    this.flow = flow;
    this.mue0 = mue0;
    pathsFromPlace = new HashMap<>();
    if (isNotCorrect()) {
      throw new IllegalArgumentException("This is no valid petrinet");
    }
    setVectors();
    setInitialBoundedness();
    setPaths();
    //TODO: create coverability graph in here
  }

  public Marking getMue0() {
    return this.mue0;
  }

  public List<Place> getPlaces() {
    return places;
  }

  public List<Transition> getTransitions() {
    return transitions;
  }

  public List<Edge> getFlow() {
    return flow;
  }

  public Matrix getBackwardMatrix() {
    return backwardMatrix;
  }

  public Matrix getForwardMatrix() {
    return forwardMatrix;
  }

  public Matrix getIncidenceMatrix() {
    return incidenceMatrix;
  }

  //TODO  return new Petrinet with an extra place
  //TODO: change vector mue 0
  private void addPlace(Place place) {
    places.add(place);
  }

  private void addTransition(Transition transition) {
    this.transitions.add(transition);
  }

  private void addEdge(Edge<? extends Node, ? extends Node> edge) {
    flow.add(edge);
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
    forwardMatrix = new Matrix(transitions.stream().map(t -> t.getPostSet().vector()).toArray(
        Vector[]::new), false);
    backwardMatrix = new Matrix(transitions.stream().map(t -> t.getPreSet().vector()).toArray(
        Vector[]::new), false);
    incidenceMatrix = forwardMatrix = new Matrix(
        transitions.stream().map(t -> t.getPostSet().vector().sub(t.getPreSet().vector())).toArray(
            Vector[]::new), false);
  }

  /**
   * Initializes every node with the boundedness of the initial marking mue0.
   */
  protected void setInitialBoundedness() {
    for (int i = 0; i < places.size(); i++) {
      places.get(i).setBoundedness(mue0.get(i));
    }
  }

  private void setPaths() {
    places.forEach(place -> pathsFromPlace.put(place, new HashSet<>()));
    flow.forEach(edge -> {
      if (edge.getFrom() instanceof Place) {
        pathsFromPlace.get((Place) edge.getFrom()).addAll(
            flow.stream().filter(otherEdge -> otherEdge.getFrom().equals(edge.getTo()))
                .map(Edge<Transition, Place>::getTo)
                .collect(Collectors.toCollection(HashSet::new))
        );
      }
    });
  }

  // wip, does not work
  /* private void addConnectedPlaces(Place place, Set<Place> alreadyConnectedPlaces) {
    // schaue für jede bisherige erreichbare stelle, was ihre nächsten stellen sind
    alreadyConnectedPlaces.forEach(connectedPlace -> {
      //set der nächsten erreichbaren stellen von connected place
      Set<Place> setOfNextPlaces = pathsFromPlace.get(
          // da connected place nicht das gleiche objekt, muss es gefiltert werden aus dem key set
          pathsFromPlace.keySet().stream()
              .filter(connectedPlace::equals)
              .findFirst()
              .get()).stream()
          .filter(p -> alreadyConnectedPlaces.stream().noneMatch(p::equals))
          .collect(Collectors.toSet());

      // wenn neue stellen dabei
      System.out.println(place + " " + setOfNextPlaces.toString());
      if (setOfNextPlaces.size() > 0) {
        //füge diese zu already connected hinzu
        //stellen sind final, also füge folgestellen zur map hinzu
        places.stream()
            //mappe auf die stellen ihre sets
            .map(p -> pathsFromPlace
                .get(pathsFromPlace.keySet().stream()
                    //finde wieder das richtige set für p
                    .filter(p::equals)
                    .findFirst().get()))
            //schaue im nachbereich, ob place drin ist
            .filter(set -> set.stream().anyMatch(place::equals))
            //füge die neuen places hinzu
            .forEach(p -> {
              pathsFromPlace.get(places.stream().filter(place::equals).findFirst().get())
                  .addAll(setOfNextPlaces);
            });
        pathsFromPlace.get(places.stream().filter(place::equals).findFirst().get())
            .addAll(setOfNextPlaces);
      }
    });
  } */

  /**
   * A petrinet is correct if: places and transitions are finite, linearly ordered, t contains at
   * least one element and the net is connected.
   *
   * @return true, when this net is correct
   */
  protected boolean isNotCorrect() {
    return !isSameLength() || !(transitions.size() > 0) || !isConnected();
  }

  private boolean isSameLength() {
    return mue0.getLength() == places.size();
  }

  private boolean isConnected() {
    return transitions.stream()
        .filter(transition ->
            (int) flow.stream()
                .filter(
                    edge -> edge.getFrom().equals(transition) || edge.getTo().equals(transition))
                .count() >= 1)
        .count() == transitions.size()
        && places.stream()
        .filter(place ->
            (int) flow.stream()
                .filter(edge -> edge.getFrom().equals(place) || edge.getTo().equals(place))
                .count() >= 1)
        .count() == places.size();
  }

  /**
   * Check if net is strong connected.
   *
   * @return - true, when strongly connected
   */
  public boolean isStronglyConnected() {
    return flow.stream().allMatch(this::hasReversedPath);
  }

  private boolean hasReversedPath(Edge edge) {
    return flow.stream().filter(edge2 -> edge2.getFrom().equals(edge.getTo())).anyMatch(
        edge2 -> edge2.getTo() == edge.getFrom() || hasReversedPath(edge2.getTo(), edge.getFrom()));
  }

  private boolean hasReversedPath(Node from, Node to) {
    return flow.stream().filter(nextEdge -> nextEdge.getFrom().equals(to)).anyMatch(
        nextEdge -> nextEdge.getTo() == from || hasReversedPath(nextEdge.getTo(), to));
  }

  private boolean isConnectedToEveryPlace(Place place) {
    return places.stream().allMatch(p -> pathsFromPlace.get(place).stream().anyMatch(p::equals));
  }

  /**
   * Checks the petrinet for loops. A loop goes to a transition and vice versa.
   *
   * @return true when any edge has a reversed pair
   */
  public boolean containsLoop() {
    return flow.stream()
        .anyMatch(edgeTowards -> flow.stream()
            .anyMatch(edgeBackwards -> edgeTowards.getTo().equals(edgeBackwards.getFrom())
                && edgeTowards.getFrom().equals(edgeBackwards.getTo())));
  }

  /**
   * Checks whether this net is alive and bounded. If so, you can call it well-formed.
   *
   * @return - true, when it is well-formed
   */
  public boolean isWellFormed() {
    return getLiveness() == 2 && getBoundedness() >= 0;
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
        new Marking(newPlaces.size()));
  }

  /**
   * Return a dual-petrinet with markings.
   *
   * @param markings new combination of markings for new places
   * @return a new petrinet but with markings
   */
  public Petrinet dual(Marking markings) {
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

  public CoverabilityGraph createCoverabilityGraph() {
    return new CoverabilityGraph(mue0, this.name + "Cov", this);
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
