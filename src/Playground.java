import exception.WrongDimensionException;
import graph.Vector;

/**
 * This class is for fast testing inside my IDE.
 */
public class Playground {

  public static void main(String[] args) throws WrongDimensionException {
    Petrinet petrinet = PetriReader.createPetriNetAndMarkings("s1:t1;s2:;;t1:s2;;", "2,0");
    CoverabilityGraph covGraph = PetriReader.createCoverabilityGraph(petrinet, "covGraph");
    petrinet.setVectors();
    System.out.println(petrinet);
    System.out.println(covGraph);
    System.out.println(petrinet.getBoundedness());
  }

}
