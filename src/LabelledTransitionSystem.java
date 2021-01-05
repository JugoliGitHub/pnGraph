import exception.WrongDimensionException;
import graph.Edge;
import graph.Place;
import graph.Transition;
import graph.Vector;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LabelledTransitionSystem extends CoverabilityGraph {

  private final Vector capacity;

  /**
   * Constructor
   *
   * @param mue0 marking at the beginning
   * @param name name of the graph
   * @param petrinet corresponding petrinet
   */
  public LabelledTransitionSystem(Vector mue0, String name, Petrinet petrinet)
      throws WrongDimensionException {
    super(mue0, name);
    this.petrinet = petrinet;
    capacity = petrinet.getCapacity();
    ArrayList<Vector> path = new ArrayList<>();
    path.add(mue0);
    go(mue0, path);
    this.petrinet.getTransitions().forEach(this::setLiveness);
  }

  @Override
  protected Optional<Vector> fire(Vector mue, Transition transition) throws WrongDimensionException {
    Vector newMue = new Vector(mue.getLength());
    newMue = newMue.add(mue);
    if (transition.getOutput().getLength() == 0 || transition.getInput().getLength() == 0) {
      throw new IllegalArgumentException("Initialize in- and output first.");
    } else if (transition.getInput().lessEquals(mue)
          && newMue.sub(transition.getInput()).add(transition.getOutput())
          .lessEquals(capacity)) {
        newMue = newMue.sub(transition.getInput()).add(transition.getOutput());
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
