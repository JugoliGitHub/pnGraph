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
            "s1:t1;s2:t2;;t1:s1,s2;t2:s1;;",
        "1,0");

    System.out.println(p);
    System.out.println(p.getCoverabilityGraph());
    System.out.println(p.getReachabilityGraph(5));
    //System.out.println(p.getPlaceInvariants());
    //System.out.println("CoFallen: " + p.getPureNet().get().getMinimalSiphons());
   // System.out.println("Fallen: " + p.reversed().getPureNet().get().getMinimalSiphons());
    // System.out.println(pnUe43a.getCoverabilityGraph());
  }

}
