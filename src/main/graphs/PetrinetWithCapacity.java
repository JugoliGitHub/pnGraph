package main.graphs;

import java.util.List;
import main.graphs.objects.edges.Edge;
import main.graphs.objects.nodes.Place;
import main.graphs.objects.nodes.Transition;
import main.graphs.objects.Vector;

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