package graphs;

import graphs.objects.Vector;
import graphs.objects.edges.Edge;
import graphs.objects.nodes.Place;
import graphs.objects.nodes.Transition;
import java.util.List;

public class PetrinetWithCapacity extends Petrinet {

  private final Vector capacity;

  /**
   * Creates an empty petrinet. Should initialize mue_0, capacity (if present) and places,
   * transitions and flow.
   *
   * @param name name of the net
   */
  public PetrinetWithCapacity(String name, List<Place> places, List<Transition> transitions,
      List<Edge> flow, Vector mue0, Vector capacity) {
    super(name, places, transitions, flow, mue0);
    this.capacity = capacity;
    if (!isNotCorrect()) {
      throw new IllegalArgumentException("This is no valid petrinet. The capacity is exceeded.");
    }
  }

  public Vector getCapacity() {
    return capacity;
  }

  @Override
  protected void setInitialBoundedness() {
    for (int i = 0; i < places.size(); i++) {
      places.get(i).setBoundedness(capacity.get(i));
    }
  }

  /*for (int i = 0; i < places.size(); i++) {
    if (mue0.get(i) > capacity.get(i)) {
      throw new OutOfCapacityException("The start marking has "
          + "to be less-equal than the capacity");
    }
    places.get(i).setBoundedness(mue0.get(i));
  } */
}
