package graphs;

import graphs.objects.Marking;
import graphs.objects.edges.Edge;
import graphs.objects.nodes.Place;
import graphs.objects.nodes.Transition;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LabelledTerminalPetrinet extends Petrinet implements Labelled, Terminal {

  List<String> symbols;
  Map<Transition, String> labels;

  private Set<Marking> terminalMarkings;

  /**
   * Creates an empty petrinet. Should initialize mue0, capacity (if present) and places,
   * transitions and flow.
   *
   * @param name        name of the net
   * @param places
   * @param transitions
   * @param flow
   * @param mue0
   */
  public LabelledTerminalPetrinet(String name, List<Place> places,
      List<Transition> transitions, List<Edge> flow, Set<Marking> terminalMarkings,
      List<String> symbols, Map<Transition, String> labels, Marking mue0) {
    super(name, places, transitions, flow, mue0);
    this.terminalMarkings = terminalMarkings;
    this.symbols = symbols;
    this.labels = labels;
  }

  @Override
  public List<String> getSymbols() {
    return symbols;
  }

  @Override
  public Map<Transition, String> getLabels() {
    return labels;
  }

  @Override
  public Set<Marking> getTerminalMarkings() {
    return terminalMarkings;
  }
}
