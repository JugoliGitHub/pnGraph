package graphs.petrinet.extensions;

import java.util.List;
import java.util.Map;

public interface Labelled {

  public List<String> getSymbols();

  public Map<?, String> getLabels();

}
