import exception.NotExistingNodeException;
import graph.Edge;
import graph.Place;
import graph.Transition;
import graph.Vector;
import interfaces.AddNodes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import graph.Node;
import java.util.stream.Collectors;

public class Petrinet implements AddNodes {

  private String name;
  private List<Place> places;
  private List<Transition> transitions;
  private List<Edge> flow;
  private Vector mue0;

  public Petrinet(String name) {
    this.name = name;
    this.places = new ArrayList<>();
    this.transitions = new ArrayList<>();
    this.flow = new ArrayList<>();
  }

  public void setMue0(Vector m0) {
    this.mue0 = m0;
  }

  public Vector getMue0() {
    return this.mue0;
  }

  public void addPlace(Place place) {
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

  @Override
  public void addPlaceNodes(String[] split) throws NotExistingNodeException {
    Place place = new Place(split[0]);
    this.addPlace(place);
    if (split.length > 1) {
      String[] toNodes = split[1].split(",");
      if (toNodes.length > 0) {
        for (String toNode : toNodes) {
          this.addEdge(new Edge<>(place, new Transition(toNode)));
        }
      }
    }
  }

  @Override
  public void addTransitionNodes(String[] split) throws NotExistingNodeException {
    Transition transition = new Transition(split[0]);
    this.addTransition(transition);
    String[] toNodes = split[1].split(",");
    if (toNodes.length > 0) {
      for (String toNode : toNodes) {
        this.addEdge(new Edge<>(transition,
            places.stream().filter(place -> place.toString().equals(toNode)).findAny().orElseThrow(
                NotExistingNodeException::new)));
      }
    }
  }

  /**
   * Sets the start- and end- nodes for every node.
   */
  public void setVectors() {
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

  /**
   * -1: dead; 0: not dead; 1: weak liveness; 2: alive
   */
  public int getLiveness() {
    return Collections
        .min(transitions.stream().map(Transition::getLiveness).collect(Collectors.toList()));
  }

  //TODO: structural liveness and deadlock-free

  public void setInitialBoundesness() {
    for (int i = 0; i < places.size(); i++) {
      places.get(i).setBoundedness(mue0.get(i));
    }
  }

  public int getBoundedness() {
    return places.stream().map(Place::getBoundedness).filter(bound -> bound == -1)
        .findAny()
        .orElse(Collections
            .max(places.stream().map(Place::getBoundedness).collect(Collectors.toList())));
  }
}
