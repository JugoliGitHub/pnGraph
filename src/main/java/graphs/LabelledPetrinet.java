package graphs;

import graphs.objects.Marking;
import graphs.objects.edges.Edge;
import graphs.objects.nodes.Place;
import graphs.objects.nodes.Transition;
import java.util.List;
import java.util.Map;

public class LabelledPetrinet extends Petrinet implements Labelled{

  List<String> symbols;
  Map<Transition, String> labels;

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
  public LabelledPetrinet(String name, List<Place> places,
      List<Transition> transitions,
      List<Edge> flow, List<String> symbols, Map<Transition, String> labels, Marking mue0) {
    super(name, places, transitions, flow, mue0);
    this.symbols = symbols;
    this.labels = labels;
  }

  public List<String> getSymbols() {
    return symbols;
  }

  @Override
  public Map<Transition, String> getLabels() {
    return labels;
  }

}
