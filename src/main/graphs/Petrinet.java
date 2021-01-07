package main.graphs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import main.graphs.objects.edges.Edge;
import main.graphs.objects.nodes.Node;
import main.graphs.objects.nodes.Place;
import main.graphs.objects.nodes.Transition;
import main.graphs.objects.Vector;

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
    checkCorrectness();
    setVectors();
    setInitialBoundedness();
  }

  /***********************/
  /** Getter and Setter **/
  /***********************/
  public Vector getMue0() {
    return this.mue0;
  }

  public void addPlace(Place place) {
    //TODO: change vector mue 0
    //TODO  return new Petrinet with an extra place
    this.places.add(place);
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

  protected void checkCorrectness() {
    // TODO: check correctness
    // throw exceptions and kill user D:
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
    return new Petrinet(this.name + "Reversed", new ArrayList<>(places),
        new ArrayList<>(transitions), flow.stream().map(Edge::reverse).collect(Collectors.toList()),
        mue0.copy());
  }

  /**
   * Creates the dual-graph to this petrinet.
   *
   * @return
   */
  // public Petrinet dual() {
  public void dual() {
    // TODO: implement
    // return
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
          label.append("&bull;".repeat(mue0.get(i)));
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

  public boolean equals(Petrinet petrinet) {
    //TODO: implement
    // two nets can be equal beside their place/transition names
    return false;
  }

}
