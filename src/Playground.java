import exception.WrongDimensionException;
import graph.Vector;

/**
 * This class is for quick testing inside my IDE.
 */
public class Playground {

  public static void main(String[] args) throws WrongDimensionException {
    Petrinet petrinet = PetriReader.createPetriNetAndMarkings("s1:a,b;s2:c;s3:d;;a:s2;b:s3;c:s2,s3;d:s2;;", "1,0,0");
    CoverabilityGraph covGraph = new CoverabilityGraph(petrinet.getMue0(), "covGraph", petrinet);
    petrinet.setVectors();
    petrinet.getTransitions().forEach(t -> System.out.println(t.getLiveness()));
    System.out.println(petrinet.getBoundedness());
    System.out.println(covGraph);
  }

}
