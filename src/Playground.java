import exception.WrongDimensionException;

public class Playground {

  public static void main(String[] args) throws WrongDimensionException {
    Petrinet petrinet = PetriReader.createPetriNetAndMarkings("s1:t1;;t1:s1;;", "1");
    CoverabilityGraph covGraph = PetriReader.createCoverabilityGraph(petrinet, "covGraph");
  }

}
