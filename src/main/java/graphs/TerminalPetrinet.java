package graphs;

import graphs.objects.Marking;
import graphs.objects.edges.Edge;
import graphs.objects.nodes.Place;
import graphs.objects.nodes.Transition;
import java.util.List;
import java.util.Set;

public class TerminalPetrinet extends Petrinet implements Terminal {

  private Set<Marking> terminalMarkings;

  /**
   * Creates an empty petrinet. Should initialize mue0, capacity (if present) and places, transitions
   * and flow.
   *
   * @param name        name of the net
   * @param places
   * @param transitions
   * @param flow
   * @param mue0
   */
  public TerminalPetrinet(String name, List<Place> places,
      List<Transition> transitions,
      List<Edge> flow, Set<Marking> terminalMarkings, Marking mue0) {
    super(name, places, transitions, flow, mue0);
    this.terminalMarkings = terminalMarkings;
  }

  @Override
  public Set<Marking> getTerminalMarkings() {
    return terminalMarkings;
  }

}
