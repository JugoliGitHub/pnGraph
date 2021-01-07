package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import main.graph.Edge;
import main.graph.Node;
import main.graph.Place;
import main.graph.Transition;
import main.graph.Vector;

public class Petrinet {

  private final String name;
  private final List<Place> places;
  private final List<Transition> transitions;
  private final List<Edge> flow;
  private final Vector mue0;


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

  public Vector getMue0() {
    return this.mue0;
  }

  public void addPlace(Place place) {
    //TODO: change vector mue 0
    //TODO new Petrinet with an extra place
    this.places.add(place);
  }

  /***********************/
  /** Getter and Setter **/
  /***********************/
  public List<Place> getPlaces() {
    return places;
  }

  public void addTransition(Transition transition) { this.transitions.add(transition); }

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
  protected void setVectors() {
    new Thread(() ->
        places.forEach(place -> {
          int[] arrIn = new int[transitions.size()];
          int[] arrOut = new int[transitions.size()];
          flow.forEach(edge -> {
            Node from = edge.getFrom();
            Node to = edge.getTo();
            if (place.equals(to) && from.getClass() == Transition.class) {
              arrIn[from.indexIn(transitions)] += 1;
            } else if (place.equals(from) && to.getClass() == Transition.class) {
              arrOut[to.indexIn(transitions)] += 1;
            }
          });
          place.setInput(new Vector(arrIn));
          place.setOutput(new Vector(arrOut));
        })).start();
    new Thread(() ->
        transitions.forEach(transition -> {
          int[] arrIn = new int[places.size()];
          int[] arrOut = new int[places.size()];
          flow.forEach(edge -> {
            Node from = edge.getFrom();
            Node to = edge.getTo();
            if (transition.equals(to) && from.getClass() == Place.class) {
              arrIn[from.indexIn(places)] += 1;
            } else if (transition.equals(from) && to.getClass() == Place.class) {
              arrOut[to.indexIn(places)] += 1;
            }
          });
          transition.setInput(new Vector(arrIn));
          transition.setOutput(new Vector(arrOut));
        })).start();
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
