import exception.NotExistingNodeException;
import exception.OutOfCapacityException;
import graph.Edge;
import graph.Place;
import graph.Transition;
import graph.Vector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import graph.Node;
import java.util.stream.Collectors;

public class Petrinet {

  private final String name;
  private final List<Place> places;
  private final List<Transition> transitions;
  private final List<Edge> flow;
  private Vector mue0;
  private Vector capacity;

  /**
   * Constructor of a Petrinet. Creates an empty petrinet.
   * Should initialize mue_0, capacity (if present) and places, transitions and flow.
   * @param name name of the net
   */
  public Petrinet(String name) {
    this.name = name;
    this.places = new ArrayList<>();
    this.transitions = new ArrayList<>();
    this.flow = new ArrayList<>();
    this.mue0 = new Vector(0);
    this.capacity = new Vector(0);
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

  public void setCapacity(Vector c) { this.capacity = c; }

  public Vector getCapacity() { return capacity; }

  /**
   * Adds place nodes to the petrinet.
   * @param split split of transition part of pn-string
   * @throws NotExistingNodeException throws a runtime exception, in case the node does not exist
   */
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

  /**
   * Adds transition nodes to the petrinet.
   * @param split split of place part of pn-string.
   * @throws NotExistingNodeException throws a runtime exception, in case the node does not exist
   */
  public void addTransitionNodes(String[] split) throws NotExistingNodeException {
    Transition transition = new Transition(split[0]);
    this.addTransition(transition);
    if (split.length > 1) {
      String[] toNodes = split[1].split(",");
      for (String toNode : toNodes) {
        this.addEdge(new Edge<>(transition,
            places.stream().filter(place -> place.toString().equals(toNode)).findAny()
                .orElseThrow(
                    NotExistingNodeException::new)));
      }
    }
  }

  /**
   * Sets the start- and end-nodes for every node.
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
   * Initializes every node with the boundedness of the initial marking mue_0.
   */
  public void setInitialBoundedness() {
    if (!capacity.equals(new Vector(0))) {
      for (int i = 0; i < places.size(); i++) {
        if (mue0.get(i) > capacity.get(i)) throw new OutOfCapacityException("The start marking has "
            + "to be less-equal than the capacity");
        places.get(i).setBoundedness(mue0.get(i));
      }
    } else {
      for (int i = 0; i < places.size(); i++) {
        places.get(i).setBoundedness(mue0.get(i));
      }
    }
  }

  /**
   * Returns the maximal value of a bounded place.
   * @return -1 for unbounded (omega), k as the maximal bound
   */
  public int getBoundedness() {
    return places.stream().map(Place::getBoundedness).filter(bound -> bound == -1)
        .findAny()
        .orElse(Collections
            .max(places.stream().map(Place::getBoundedness).collect(Collectors.toList())));
  }

}
