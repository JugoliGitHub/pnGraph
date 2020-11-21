import exception.NotExistingNodeException;
import graph.Edge;
import graph.Place;
import graph.Transition;
import graph.Vector;
import interfaces.AddNodes;
import java.util.ArrayList;
import java.util.List;
import graph.Node;

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

  public void addTransition(Transition transition) {
    this.transitions.add(transition);
  }

  public List<Transition> getTransitions() {
    return transitions;
  }

  public void addEdge(Edge<? extends Node, ? extends Node> edge) {
    flow.add(edge);
  }

  @Override
  public void addPlaceNodes(String[] split) throws NotExistingNodeException {
    Place place = new Place(split[0], places.size());
    this.addPlace(place);
    String[] toNodes = split[1].split(",");
    if (toNodes.length > 0) {
      for (String toNode : toNodes) {
        this.addEdge(new Edge<>(place, new Transition(toNode)));
        /*this.addEdge(new Edge<>(place,
            transitions.stream().filter(transition -> transition.toString().equals(toNode))
                .findAny().orElseThrow(NotExistingNodeException::new)));*/
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

  public void setVectors() {
    places.forEach(place -> {
      int[] arrPre = new int[transitions.size()];
      int[] arrPost = new int[transitions.size()];
      flow.forEach(edge -> {
        Node from = edge.getFrom();
        Node to = edge.getTo();
        if (place.equals(to) && from.getClass() == Transition.class) {
          arrPre[transitions.indexOf(from)] += 1;
        } else if (place.equals(from) && to.getClass() == Transition.class) {
          arrPost[transitions.indexOf(to)] += 1;
        }
      });
      place.setPreVector(new Vector(arrPre));
      place.setPostVector(new Vector(arrPost));
    });
    transitions.forEach(transition -> {
      int[] arrPre = new int[places.size()];
      int[] arrPost = new int[places.size()];
      flow.forEach(edge -> {
        Node from = edge.getFrom();
        Node to = edge.getTo();
        if (transition.equals(to) && from.getClass() == Place.class) {
          arrPre[places.indexOf(from)] += 1;
        } else if (transition.equals(from) && to.getClass() == Place.class) {
          arrPost[places.indexOf(to)] += 1;
        }
      });
      transition.setPreVector(new Vector(arrPre));
      transition.setPostVector(new Vector(arrPost));
    });
  }

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
    return out.toString();
  }
}
