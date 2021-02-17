package graphs.petrinet;

import graphs.objects.Marking;
import graphs.petrinet.extensions.PetrinetWithCapacity;

/**
 * A subclass of petrinet with capacity. Every Place has a capacity of one.
 */
class ConditionEventSystem extends PetrinetWithCapacity {

  /**
   * Creates an empty petrinet. Should initialize mue_0, capacity (if present) and places,
   * transitions and flow after constructing.
   *
   * @param petrinet a normal petrinet to convert in condition event system
   */
  public ConditionEventSystem(Petrinet petrinet) {
    super(petrinet.name, petrinet.places, petrinet.transitions, petrinet.flow, petrinet.mue0,
        new Marking(petrinet.mue0.getDimension(), 1));
  }

}
