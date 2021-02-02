package graphs.petrinet;

import graphs.objects.Marking;
import graphs.objects.edges.Edge;
import graphs.objects.nodes.Place;
import graphs.objects.nodes.Transition;
import graphs.petrinet.extensions.PetrinetWithCapacity;
import java.util.List;

/**
 * A subclass of petrinet with capacity. Every Place has a capacity of one.
 */
class ConditionEventSystem extends PetrinetWithCapacity {

  /**
   * Creates an empty petrinet. Should initialize mue_0, capacity (if present) and places,
   * transitions and flow after constructing.
   *
   * @param name       name of the net
   * @param conditions list of places, here called conditions
   * @param events     list of transitions, here called events
   * @param flow       connection of conditions and events
   * @param mue0       start marking
   */
  public ConditionEventSystem(String name, List<Place> conditions, List<Transition> events,
      List<Edge> flow, Marking mue0) {
    super(name, conditions, events, flow, mue0, new Marking(mue0.getDimension(), 1));
  }

}
