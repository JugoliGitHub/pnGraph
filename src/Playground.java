import exception.WrongDimensionException;
import graph.Vector;

/**
 * This class is for fast testing inside my IDE.
 */
public class Playground {

  public static void main(String[] args) throws WrongDimensionException {
    Petrinet petrinet = PetriReader.createPetriNetAndMarkings("s1:t1;s2:;;t1:s2;;", "2,0");
    CoverabilityGraph covGraph = new CoverabilityGraph(petrinet.getMue0(), "covGraph", petrinet);
    petrinet.setVectors();
    System.out.println(petrinet);
    System.out.println(covGraph);
    System.out.println(petrinet.getBoundedness());
  }

}
