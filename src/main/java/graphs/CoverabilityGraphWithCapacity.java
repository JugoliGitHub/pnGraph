package graphs;

import exception.WrongDimensionException;
import graphs.objects.Marking;
import graphs.objects.nodes.Transition;
import java.util.ArrayList;
import java.util.Optional;

/**
 * A sub-class of coverability-graph. It can only fire when the capacity is complied with the new
 * marking.
 */
public class CoverabilityGraphWithCapacity extends CoverabilityGraph {

  private final Marking capacity;

  /**
   * Constructor of a labelled-transition-system.
   *
   * @param mue0     marking at the beginning
   * @param name     name of the graph
   * @param petrinet corresponding petrinet
   */
  public CoverabilityGraphWithCapacity(Marking mue0, String name, PetrinetWithCapacity petrinet)
      throws WrongDimensionException {
    super(mue0, name);
    this.petrinet = petrinet;
    capacity = petrinet.getCapacity();
    ArrayList<Marking> path = new ArrayList<>();
    path.add(mue0);
    run(mue0, path);
    this.petrinet.getTransitions().forEach(this::setLiveness);
  }

  @Override
  protected Optional<Marking> fire(Marking mue, Transition transition)
      throws WrongDimensionException {
    Marking newMue = new Marking(mue.getDimension());
    newMue = newMue.add(mue);
    if (transition.getPostSet().getDimension() == 0 || transition.getPreSet().getDimension() == 0) {
      throw new IllegalArgumentException("Initialize in- and output first.");
    } else if (transition.getPreSet().lessEquals(mue)
        && newMue.sub(transition.getPreSet()).add(transition.getPostSet())
        .lessEquals(capacity)) {
      newMue = newMue.sub(transition.getPreSet()).add(transition.getPostSet());
      setBoundednessOfPlaces(mue, newMue);
    } else {
      return Optional.empty();
    }
    if (transition.isDead()) {
      transition.setLiveness(0);
    }
    return Optional.of(newMue);
  }

}
