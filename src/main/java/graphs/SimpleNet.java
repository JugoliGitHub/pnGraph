package graphs;

import graphs.objects.IntVector;
import graphs.objects.Marking;
import graphs.objects.Vector;
import graphs.objects.edges.Edge;
import graphs.objects.nodes.Node;
import graphs.objects.nodes.Place;
import graphs.objects.nodes.Transition;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SimpleNet extends Petrinet {


  /**
   * Creates an empty petrinet. Should initialize mue0, capacity (if present) and places, transitions
   * and flow.
   *
   * @param name        name of the net
   * @param places
   * @param transitions
   * @param flow
   * @param mue0
   */
  public SimpleNet(String name, List<Place> places,
      List<Transition> transitions,
      List<Edge> flow, Marking mue0) {
    super(name, places, transitions, flow, mue0);
    //TODO: vor und nachbereiche als mengen
    //TODO: siphons
  }

  @Override
  protected boolean isNotCorrect() {
    return !isSameLength()
        || !(transitions.size() > 0)
        || !isConnected()
        || containsLoop()
        || !containsMultiEdges();
  }

  private boolean containsMultiEdges() {
    return true;
  }

  public List<Set<Place>> getMinimalSiphons() {
    Set<Place> q = new HashSet<>();
    Set<Place> r = new HashSet<>(places);
    List<Set<Place>> siphons = new ArrayList<>();
    List<Set<Place>> old = new ArrayList<>();

    siphons(q, r, siphons, old);

    return siphons;
  }

  private void siphons(Set<Place> q, Set<Place> r, List<Set<Place>> siphons, List<Set<Place>> old) {
    while (!r.isEmpty()) {
      Set<Place> b = new HashSet<>(q);
      Place p = r.stream().findAny().get();
      r.remove(p);
      b.add(p);
      if (getAllSubsets(b).stream().noneMatch(siphons::contains)) {
        Marking m = b.stream().map(Node::getPostSet).reduce(Marking::add).get();
        IntVector v = new IntVector(transitions.size());
        for (int i = 0; i < transitions.size(); i++) {
          if (m.get(i) > 0) {
            v = v.add(i, 1);
          }
        }
        if (v.sub(b.stream().map(Node::getPreSet).reduce(Marking::add).get()).isNotNegative()) {
          siphons = minimize(siphons, b);
          siphons.add(b);
        } else {
          if (getAllSubsets(b).stream().noneMatch(old::contains)) {
            siphons(b,
                getPrePlaceSetFromMarking(getPreVectorFromSet(b).intVector().sub(getPostVectorFromSet(b))),
                siphons, old);
            old = minimize(old, b);
            old.add(b);
          }
        }
      }
    }
  }

  private static ArrayList<Set<Place>> minimize(List<Set<Place>> setList, Set<Place> b) {
    return setList.stream()
        .filter(set -> set.stream().noneMatch(place -> b.stream().anyMatch(place::equals)))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private static Set<Place> getAllSubsets(Set<Place> set) {
    int n = set.size();
    List<Place> list = List.copyOf(set);
    Set<Place> subset = new HashSet<>();

    for (int i = 0; i < (1 << n); i++) {
      for (int j = 0; j < n; j++) {
        if ((i & (1 << j)) > 0) {
          subset.add(list.get(j));
        }
      }
    }
    return subset;
  }

  //TODO auslagern auf Objekte, sodass places ihren vor bzw nachbereich zugeordnet bekommen
  private Set<Place> getPrePlaceSetFromMarking(Vector mue) {
    Marking marking = IntStream.range(0, transitions.size())
        .mapToObj(i -> mue.get(i) > 0 ? Optional.of(transitions.get(i)) : Optional.empty())
        .filter(Optional::isPresent)
        .map(o -> ((Transition) o.get()).getPreSet())
        .reduce(Marking::add).get();
    return IntStream.range(0, places.size())
        .mapToObj(i -> marking.get(i) > 0 ? Optional.of(places.get(i)) : Optional.empty())
        .filter(Optional::isPresent)
        .map(o -> (Place) o.get())
        .collect(Collectors.toSet());
  }

  private Marking getPreVectorFromSet(Set<Place> b) {
    return places.stream()
        .map(place -> b.contains(place) ? place.getPreSet() : new Marking(transitions.size()))
        .reduce(Marking::add)
        .get();
  }

  private Marking getPostVectorFromSet(Set<Place> b) {
    return places.stream()
        .map(place -> b.contains(place) ? place.getPostSet() : new Marking(transitions.size()))
        .reduce(Marking::add)
        .get();
  }

  public boolean isPlaceNet() {
    return false;
  }

  public boolean isTransitionNet() {
    return false;
  }

  public boolean isFreeChoice() {
    return false;
  }
}
