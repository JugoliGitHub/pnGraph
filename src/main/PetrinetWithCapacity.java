package main;

import java.util.List;
import main.graph.Edge;
import main.graph.Place;
import main.graph.Transition;
import main.graph.Vector;

public class PetrinetWithCapacity extends Petrinet {

  //TODO: auslagern in PnWithCapacity
  // pn -> pn with capacity -> b/e system
  // be system ohne schlingen und mehrfachkanten
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


  /*for (int i = 0; i < places.size(); i++) {
    if (mue0.get(i) > capacity.get(i)) {
      throw new OutOfCapacityException("The start marking has "
          + "to be less-equal than the capacity");
    }
    places.get(i).setBoundedness(mue0.get(i));
  } */
}
