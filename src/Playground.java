import exception.WrongDimensionException;
import graph.CoverabilityGraphEdge;
import graph.Vector;
import java.io.IOException;

/**
 * This class is for quick testing inside my IDE.
 */
public class Playground {

  public static void main(String[] args) throws WrongDimensionException {
    //Petrinet petrinet = PetriReader.createPetriNetAndMarkings("s1:t3,t4;s2:t3;s3:t4;s4:t1,t2;s5:;;" +
    // "t1:s1,s5;t2:s1;t3:s3,s4;t4:s2,s4;;", "1,1,0,0,0");
    Petrinet petrinet = PetriReader.createPetriNetAndMarkings("s1:t1;s2:t2,t3;;t1:s2;t2:;t3:s1;;", "1,1",
        new Vector(new int[]{2, 1}));

    // Petrinet petrinet = PetriReader
    //   .createPetriNetAndMarkings("s1:t1,t4;s2:t2,t3,t4;;t1:s2;t2:s1;t3:s1,s2;t4:;;", "1,0");
    LabelledTransitionSystem covGraph = new LabelledTransitionSystem(petrinet.getMue0(), "covGraph", petrinet);
    System.out.println(petrinet);
    System.out.println(covGraph);
    System.out
        .println("Bounded: " + (petrinet.getBoundedness() == -1 ? "ω" : petrinet.getBoundedness()));
    System.out.println("Liveness: " + petrinet.getLiveness());
  }

}
