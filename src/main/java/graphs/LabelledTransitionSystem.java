package graphs;

import graphs.objects.Marking;
import java.util.List;
import java.util.Map;

public class LabelledTransitionSystem extends CoverabilityGraph implements Labelled{

  private List<String> labels;
  private Map<Marking, String> markingStringMap;

  public LabelledTransitionSystem(Marking mue0, String name, Petrinet petrinet) {
    super(mue0, name, petrinet);
  }

  @Override
  public List<String> getSymbols() {
    return labels;
  }

  @Override
  public Map<Marking, String> getLabels() {
    return markingStringMap;
  }
}
