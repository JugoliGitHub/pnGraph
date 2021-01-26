package graphs;

import graphs.objects.nodes.Transition;
import java.util.List;
import java.util.Map;

public interface Labelled {

  public List<String> getSymbols();

  public Map<Transition, String> getLabels();

}
