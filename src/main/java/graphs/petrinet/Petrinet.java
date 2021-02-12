package graphs.petrinet;

import graphs.coverabilitygraphs.CoverabilityGraph;
import graphs.objects.IntVector;
import graphs.objects.Marking;
import graphs.objects.Matrix;
import graphs.objects.Vector;
import graphs.objects.edges.Edge;
import graphs.objects.nodes.Node;
import graphs.objects.nodes.Place;
import graphs.objects.nodes.Transition;
import graphs.petrinet.extensions.PurePetrinet;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The petrinet class.
 */
public class Petrinet {

  protected final String name;
  protected final List<Place> places;
  protected final List<Transition> transitions;
  protected final List<Edge> flow;
  protected final Map<Place, Set<Place>> pathsFromPlace;
  protected Marking mue0;
  protected Matrix incidenceMatrix;
  protected Matrix forwardMatrix;
  protected Matrix backwardMatrix;
  protected CoverabilityGraph coverabilityGraph;

  /**
   * Creates an empty petrinet. Will initialize mue0, places, transitions and flow.
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
    this.coverabilityGraph = new CoverabilityGraph(mue0, this.name + "Cov", this);
  }

  public Marking getMue0() {
    return this.mue0;
  }

  public void setMue0(Marking mue0) {
    this.mue0 = mue0;
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

  /**
   * Sets the start- and end-nodes for every node. Also creates matrices of this petrinet.
   */
  private void setVectors() {
    places.forEach(place -> place.setVectors(flow, transitions, transitions.size()));
    transitions.forEach(transition -> transition.setVectors(flow, places, places.size()));

    forwardMatrix = new Matrix(transitions.stream().map(t -> t.getPostSet().intVector())
        .toArray(IntVector[]::new), false);
    backwardMatrix = new Matrix(transitions.stream().map(t -> t.getPreSet().intVector())
        .toArray(IntVector[]::new), false);
    incidenceMatrix = new Matrix(transitions.stream()
        .map(t -> t.getPostSet().intVector().sub(t.getPreSet()))
        .toArray(IntVector[]::new), false);
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

  /**
   * A petrinet is correct if: places and transitions are finite, linearly ordered, t contains at
   * least one element and the net is connected.
   *
   * @return true, when this net is correct
   */
  protected boolean isNotCorrect() {
    return isNotSameLength() || !(transitions.size() > 0);
  }

  protected final boolean isNotSameLength() {
    return mue0.getDimension() != places.size();
  }

  protected final boolean isConnected() {
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

  //TODO: structural liveness and deadlock-free

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
    transitions.forEach(t -> System.out.println(t.getLabel() + " " + t.getLiveness()));
    return Collections
        .min(transitions.stream().map(Transition::getLiveness).collect(Collectors.toList()));
  }

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

  public CoverabilityGraph getCoverabilityGraph() {
    return coverabilityGraph;
  }

  public List<Vector> getTransitionInvariants() {
    return incidenceMatrix.transposed().minInvariants();
  }

  public List<Vector> getPlaceInvariants() {
    return incidenceMatrix.minInvariants();
  }

  //TODO: implement
  public List<Set<Place>> getCluster() {
    return Collections.emptyList();
  }

  /**
   * Returns the reversed net. That means that the flow changes its direction.
   *
   * @return a reversed petrinet
   */
  public Petrinet reversed() {
    return new Petrinet(this.name + "Reversed", List.copyOf(places), List.copyOf(transitions),
        flow.stream().map(Edge::reverse).map(edge -> {
          if (edge.getFrom() instanceof Place) {
            Place from = places.stream().filter(edge.getFrom()::equals).findFirst().get();
            Transition to = transitions.stream().filter(edge.getTo()::equals).findFirst().get();
            return new Edge<>(from, to);
          } else {
            Transition from = transitions.stream().filter(edge.getFrom()::equals).findFirst().get();
            Place to = places.stream().filter(edge.getTo()::equals).findFirst().get();
            return new Edge<>(from, to);
          }
        }).collect(Collectors.toList()), mue0.copy());
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
    return new Petrinet(this.name + "Dual", newPlaces, newTransitions,
        flow.stream().map(edge -> {
          if (edge.getFrom() instanceof Transition) {
            return new Edge<>(newPlaces.stream()
                .filter(place -> edge.getFrom().getLabel().equals(place.getLabel())).findFirst()
                .get(), newTransitions.stream()
                .filter(transition -> edge.getTo().getLabel().equals(transition.getLabel()))
                .findFirst().get());
          } else {
            return new Edge<>(newTransitions.stream()
                .filter(transition -> edge.getFrom().getLabel().equals(transition.getLabel()))
                .findFirst()
                .get(), newPlaces.stream()
                .filter(place -> edge.getTo().getLabel().equals(place.getLabel()))
                .findFirst().get());
          }
        }).collect(Collectors.toList()), new Marking(newPlaces.size()));
  }

  private List<Place> reverseTransitionsToPlaces() {
    return transitions.stream().map(transition -> new Place(transition.getLabel()))
        .collect(Collectors.toList());
  }

  private List<Transition> reversePlacesToTransitions() {
    return places.stream().map(place -> new Transition(place.getLabel()))
        .collect(Collectors.toList());
  }

  /**
   * Returns this petrinet as a pure petrinet, when it can be created. The optional is empty when it
   * could not be created.
   *
   * @return - optional of a pure petrinet
   */
  public Optional<PurePetrinet> getPureNet() {
    try {
      return Optional
          .of(new PurePetrinet(this.name, this.places, this.transitions, this.flow, this.mue0));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  /**
   * Returns a new petrinet with terminal markings
   *
   * @param terminalMarkings
   * @return
   */
  public Petrinet getNormalform(Set<Marking> terminalMarkings) {
    List<Place> placesIO = new ArrayList<>(places);
    Place sIn = new Place("sIn");
    Place sOut = new Place("sOut");
    Place sRun = new Place("sRun");
    placesIO.addAll(List.of(sIn, sOut, sRun));

    Marking mue0io = new Marking(placesIO.size()).add(places.size(), 1);

    List<Transition> transitionsio = new ArrayList<>(transitions);
    List<Edge> flowio = new ArrayList<>(flow);
    transitionsio.forEach(
        transition -> flowio
            .addAll(List.of(new Edge(transition, sRun), new Edge(sRun, transition))));
    List<Transition> transitionsStart = transitionsio.stream()
        .filter(transition -> transition.getPreSet().lessEquals(mue0))
        .map(transition -> {
          Transition newTransition = new Transition(transition.getLabel() + "'");

          flowio.addAll(List.of(new Edge(sIn, newTransition), new Edge(newTransition, sRun)));
          IntStream.range(0, places.size())
              .filter(i -> mue0.sub(transition.getPreSet()).add(transition.getPostSet()).get(i) > 0)
              .forEach(i -> flowio.add(new Edge(newTransition, places.get(i))));

          return newTransition;
        })
        .collect(Collectors.toList());

    List<Transition> transitionsEnd = transitionsio.stream()
        .filter(
            transition -> terminalMarkings.stream().anyMatch(transition.getPostSet()::lessEquals))
        .map(transition -> terminalMarkings.stream()
            .filter(transition.getPostSet()::lessEquals)
            .map(terminalMarking -> new SimpleEntry<Transition, Marking>(transition,
                terminalMarking))
            .collect(Collectors.toList()))
        .map(list -> {
          if (list.size() == 1) {
            Entry<Transition, Marking> entry = list.get(0);
            Transition newTransition = new Transition(entry.getKey().getLabel() + "''");
            flowio.addAll(List.of(new Edge(sRun, newTransition), new Edge(newTransition, sOut)));

            IntStream.range(0, places.size())
                .filter(i ->
                    entry.getValue().sub(entry.getKey().getPostSet())
                        .add(entry.getKey().getPreSet())
                        .get(i) > 0)
                .forEach(i -> flowio.add(new Edge(places.get(i), newTransition)));
            return List.of(newTransition);
          } else if (list.size() > 1) {
            List<Transition> listReturn = new ArrayList<>();
            for (int j = 0; j < list.size(); j++) {
              Entry<Transition, Marking> entry = list.get(j);
              Transition newTransition = new Transition(entry.getKey().getLabel() + Character
                  .toString('a' + j) + "''");
              flowio.addAll(List.of(new Edge(sRun, newTransition), new Edge(newTransition, sOut)));

              IntStream.range(0, places.size())
                  .filter(i ->
                      entry.getValue().sub(entry.getKey().getPostSet())
                          .add(entry.getKey().getPreSet())
                          .get(i) > 0)
                  .forEach(i -> flowio.add(new Edge(places.get(i), newTransition)));

              listReturn.add(newTransition);
            }
            return listReturn;
          }
          return new ArrayList<Transition>();
        })
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
    transitionsio.addAll(transitionsStart);
    transitionsio.addAll(transitionsEnd);
    return new Petrinet(name + "Normal", placesIO, transitionsio, flowio, mue0io);
  }

  @Override
  public String toString() {
    StringBuilder out = createPlacesOfString();
    transitions.forEach(
        transition -> out.append("  \"").append(transition.toString()).append("\" [shape=box];\n"));
    flow.forEach(edge -> out.append(edge.toString()));
    out.append("}");
    return out.append("\n").toString();
  }

  protected StringBuilder createPlacesOfString() {
    StringBuilder out = new StringBuilder(
        String.format("digraph %s{\nnode[shape=circle];\n", name));
    for (int i = 0; i < places.size(); i++) {
      if (mue0.get(i) == 0) {
        out.append("  \"").append(places.get(i).toString()).append("\";\n");
      } else {
        StringBuilder label = new StringBuilder();
        if (mue0.get(i) <= 6) {
          for (int j = 0; j < mue0.get(i); j++) {
            label.append("&bull;");
          }
        } else {
          label.append(mue0.get(i));
        }
        out.append("  \"").append(places.get(i).toString()).append("\" [label=\"").append(label)
            .append("\" xlabel=\"").append(places.get(i).toString()).append("\"];\n");
      }
    }
    return out;
  }

  /**
   * Checks whether two petrinets are same or not.
   *
   * @param petrinet other petrinet
   * @return boolean if true
   */
  public boolean equals(Petrinet petrinet) {
    // TODO: implement
    // two nets can be equal beside their place/transition names
    return false;
  }

}
