import graphs.petrinet.Petrinet;

/**
 * This class is for quick testing inside my IDE.
 */
public class Playground {

  /**
   * Add here custom nets and co.
   *
   * @param args arguments
   */
  public static void main(String[] args) {
    Petrinet p = PetriReader
        .createPetriNetAndMarkings("pn",
            "s1:t1,t1,t1,t2,t2,t3;;t1:s1,s1,s1;t2;t3:s1",
            "3");

    System.out.println(p);
    System.out.println(p.getCoverabilityGraph());
    System.out.println(p.getLiveness());
    // System.out.println(pnUe43a.getCoverabilityGraph());
  }

}
