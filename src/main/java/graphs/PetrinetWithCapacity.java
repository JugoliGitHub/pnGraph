package graphs;

import graphs.objects.Marking;
import graphs.objects.edges.Edge;
import graphs.objects.nodes.Place;
import graphs.objects.nodes.Transition;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PetrinetWithCapacity extends PurePetrinet {

  private final Marking capacity;

  /**
   * Initializes mue_0, capacity (if present) and places,
   * transitions and flow.
   *
   * @param name name of the net
   */
  public PetrinetWithCapacity(String name, List<Place> places, List<Transition> transitions,
      List<Edge> flow, Marking mue0, Marking capacity) {
    super(name, places, transitions, flow, mue0);
    this.capacity = capacity;
    IntStream.range(0, places.size()).forEach(i -> places.get(i).setBoundedness(capacity.get(i)));
    IntStream.range(0, places.size()).forEach(i -> places.get(i).setCapacity(capacity.get(i)));
  }

  public Marking getCapacity() {
    return capacity;
  }

  @Override
  public CoverabilityGraph getCoverabilityGraph() {
    return new CoverabilityGraphWithCapacity(mue0, this.name + "Cov", this);
  }

  /**
   * Returns a new petrinet without capacity.
   *
   * @return a new petrinet
   */
  public Petrinet getPetrinet() {
    List<Place> newPlaces = new ArrayList<>(places);
    newPlaces
        .addAll(places.stream().map(place -> new Place(place.getLabel() + "c")).collect(
            Collectors.toList()));

    Marking newMue0 = new Marking(places.size() * 2);
    for (int i = 0; i < places.size(); i++) {
      newMue0 = newMue0.add(i, mue0.get(i));
      newMue0 = newMue0.add(places.size() + i, places.get(i).getCapacity() - mue0.get(i));
    }

    List<Edge> newFlow = new ArrayList<>(flow);
    newFlow.addAll(flow.stream().map(edge -> {
      if (edge.getFrom() instanceof Place) {
        return new Edge<>(edge.getTo(),
            newPlaces.get(places.indexOf((Place) edge.getFrom()) + places.size()));
      } else {
        return new Edge<>(newPlaces.get(places.indexOf((Place) edge.getTo()) + places.size()),
            edge.getFrom());
      }
    }).collect(Collectors.toList()));

    return new Petrinet(this.name + "Complement", newPlaces, new ArrayList<>(transitions),
        newFlow, newMue0);
  }

}
