package graphs;

import java.util.List;
import graphs.objects.Vector;
import graphs.objects.edges.Edge;
import graphs.objects.nodes.Place;
import graphs.objects.nodes.Transition;

class ConditionEventSystem extends PetrinetWithCapacity {

  /**
   * Creates an empty petrinet. Should initialize mue_0, capacity (if present) and places,
   * transitions and flow.
   *
   * @param name        name of the net
   * @param conditions
   * @param events
   * @param flow
   * @param mue0
   */
  public ConditionEventSystem(String name, List<Place> conditions, List<Transition> events,
      List<Edge> flow, Vector mue0) {
    super(name, conditions, events, flow, mue0, new Vector(mue0.getLength(), 1));
  }

  @Override
  protected void checkCorrectness() {
    //TODO: no loops, multiple edges
  }
}
