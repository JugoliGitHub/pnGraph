package graphs;

import graphs.objects.Marking;
import graphs.objects.edges.Edge;
import graphs.objects.nodes.Place;
import graphs.objects.nodes.Transition;
import java.util.List;

public class PetrinetWithCapacity extends SimpleNet {

  private final Marking capacity;

  /**
   * Creates an empty petrinet. Should initialize mue_0, capacity (if present) and places,
   * transitions and flow.
   *
   * @param name name of the net
   */
  public PetrinetWithCapacity(String name, List<Place> places, List<Transition> transitions,
      List<Edge> flow, Marking mue0, Marking capacity) {
    super(name, places, transitions, flow, mue0);
    this.capacity = capacity;
  }

  public Marking getCapacity() {
    return capacity;
  }

  @Override
  protected void setInitialBoundedness() {
    for (int i = 0; i < places.size(); i++) {
      places.get(i).setBoundedness(capacity.get(i));
    }
  }

}
