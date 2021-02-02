package graphs.petrinet.extensions;

import graphs.objects.IntVector;
import graphs.objects.Marking;
import graphs.objects.edges.Edge;
import graphs.objects.nodes.Place;
import graphs.objects.nodes.Transition;
import graphs.petrinet.Petrinet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A different type of petrinet. This petrinet has no loops and no multiple edges.
 */
public class PurePetrinet extends Petrinet {

  public Map<Place, Set<Transition>> preSetOfPlaces;
  public Map<Place, Set<Transition>> postSetOfPlaces;
  public Map<Transition, Set<Place>> preSetOfTransitions;
  public Map<Transition, Set<Place>> postSetOfTransitions;

  List<Set<Place>> siphons = new ArrayList<>();
  List<Set<Place>> traps = new ArrayList<>();
  List<Set<Place>> old;

  /**
   * Initialize mue0, places, transitions and flow. The pure petrinet only has no loops, so each
   * note in pre- and post-set can be saved.
   *
   * @param name        name of the net
   * @param places      places of pure net
   * @param transitions transitions
   * @param flow        edges between nodes
   * @param mue0        start marking
   */
  public PurePetrinet(String name, List<Place> places, List<Transition> transitions,
      List<Edge> flow,
      Marking mue0) {
    super(name, places, transitions, flow, mue0);
    fillMaps();
  }

  @Override
  protected boolean isNotCorrect() {
    return isNotSameLength() || !(transitions.size() > 0) || !isConnected() || containsLoop()
        || !containsMultiEdges();
  }

  //TODO: implement
  private boolean containsMultiEdges() {
    return true;
  }

  private void fillMaps() {
    preSetOfPlaces = new HashMap<>();
    postSetOfPlaces = new HashMap<>();
    preSetOfTransitions = new HashMap<>();
    postSetOfTransitions = new HashMap<>();
    IntStream.range(0, transitions.size()).forEach(j -> {
      preSetOfTransitions.put(transitions.get(j), new HashSet<>());
      postSetOfTransitions.put(transitions.get(j), new HashSet<>());
    });
    IntStream.range(0, places.size()).forEach(i -> {
      preSetOfPlaces.put(places.get(i), new HashSet<>());
      postSetOfPlaces.put(places.get(i), new HashSet<>());
      IntVector forwardRow = forwardMatrix.getRow(i);
      IntVector backwardRow = backwardMatrix.getRow(i);
      Place place = places.get(i);
      IntStream.range(0, transitions.size()).forEach(j -> {
        Transition transition = transitions.get(j);
        if (forwardRow.get(j) > 0) {
          preSetOfPlaces.get(place).add(transition);
          postSetOfTransitions.get(transition).add(place);
        }
        if (backwardRow.get(j) > 0) {
          postSetOfPlaces.get(place).add(transition);
          preSetOfTransitions.get(transition).add(place);
        }
      });
    });
  }

  public boolean isPlaceNet() {
    return transitions.stream().allMatch(t -> postSetOfTransitions.get(t).size() == 1
        && preSetOfTransitions.get(t).size() == 1);
  }

  public boolean isTransitionNet() {
    return places.stream().allMatch(p -> postSetOfPlaces.get(p).size() == 1
        && preSetOfPlaces.get(p).size() == 1);
  }

  public boolean isFreeChoice() {
    return flow.stream()
        .filter(edge -> edge.getFrom() instanceof Place && edge.getTo() instanceof Transition)
        .allMatch(edge -> postSetOfPlaces.get(edge.getFrom()).size() == 1
            || preSetOfTransitions.get(edge.getTo()).size() == 1);
  }

  @Override
  public int getLiveness() {
    return
        isPlaceNet() && isStronglyConnected() && mue0.stream().reduce(Integer::sum).getAsInt() > 0
            ? 2
            : super.getLiveness();
  }

  /**
   * An algorithm to get the minimal siphons of a pure petrinet.
   *
   * @return a list of siphons
   */
  public List<Set<Place>> getMinimalSiphons() {
    Set<Place> q = new HashSet<>();
    Set<Place> r = new HashSet<>(places);
    old = new ArrayList<>();

    siphons(q, r);

    return siphons;
  }

  private void siphons(Set<Place> q, Set<Place> r) {
    while (!r.isEmpty()) {
      Set<Place> b = new HashSet<>(q);
      Place p = r.stream().findAny().get();
      r.remove(p);
      b.add(p);
      //There is no B ′ ∈ CF mit B ′ ⊆ B
      if (siphons.stream().noneMatch(b::containsAll)) {
        //•B ⊆ B• equals B is siphon
        if (isSiphon(b)) {
          siphons = minimize(siphons, b);
          siphons.add(b);
        } else {
          //There is no U ∈ ALT mit U ⊆ B
          if (old.stream().noneMatch(b::containsAll)) {
            siphons(b, getNewPlaces(b));
            old = minimize(old, b);
            old.add(b);
          }
        }
      }
    }
  }

  private static ArrayList<Set<Place>> minimize(List<Set<Place>> setList, Set<Place> b) {
    return setList.stream()
        .filter(set -> !set.containsAll(b))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private boolean isSiphon(Set<Place> set) {
    return set.stream()
        .map(preSetOfPlaces::get)
        .collect(Collectors.toSet())
        .stream()
        .flatMap(Set::stream)
        .allMatch(element -> set.stream()
            .map(postSetOfPlaces::get)
            .collect(Collectors.toSet())
            .stream()
            .flatMap(Set::stream)
            .anyMatch(element::equals));
  }

  private Set<Place> getNewPlaces(Set<Place> set) {
    // •(•B − B•)
    return set.stream()
        .map(preSetOfPlaces::get)
        .flatMap(Set::stream)
        .filter(transition -> set.stream()
            .map(postSetOfPlaces::get)
            .flatMap(Set::stream)
            .noneMatch(transition::equals))
        .collect(Collectors.toSet())
        .stream().map(preSetOfTransitions::get)
        .flatMap(Set::stream)
        .collect(Collectors.toSet());
  }

}
