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

  @Override
  public String toString() {
    StringBuilder out = new StringBuilder(
        String.format("digraph %s{\nnode[shape=circle];\n", name));
    for (int i = 0; i < places.size(); i++) {
      if (mue0.get(i) == 0) {
        out.append("  \"").append(places.get(i).toString()).append("\";\n");
      } else {
        StringBuilder label = new StringBuilder();
        if (mue0.get(i) <= 6) {
          for (int j = 0; j < mue0.get(i); j++) {
            label.append("&bull;");
          }
        } else {
          label.append(mue0.get(i));
        }
        out.append("  \"").append(places.get(i).toString()).append("\" [label=\"").append(label)
            .append("\" xlabel=\"").append(places.get(i).toString()).append("\"];\n");
      }
    }
    transitions.forEach(
        transition -> out.append("  \"").append(transition.toString()).append("\" [shape=box"
            + " xlabel=\"").append(transition.toString()).append("\" label=\"")
            .append(this.getLabels().get(transition)).append("\"];\n"));
    flow.forEach(edge -> out.append(edge.toString()));
    out.append("}");
    return out.append("\n").toString();
  }
}
