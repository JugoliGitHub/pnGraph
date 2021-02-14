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
            "s1:t1,t2;s2:t3;s3:t4;s4:t5;s5:t6;s6:t7;s7:t7;;"
                + "t1:s2,s3;t2:s4,s5;t3:s6;t4:s7;t5:s6;t6:s7;t7:s1;;",
        "0,1,0,0,1,0,0");

    System.out.println(p);
    System.out.println(p.getCoverabilityGraph());
    System.out.println(p.getTransitionInvariants());
    System.out.println(p.getPlaceInvariants());
    System.out.println("CoFallen: " + p.getPureNet().get().getMinimalSiphons());
    System.out.println("Fallen: " + p.reversed().getPureNet().get().getMinimalSiphons());
    System.out.println(p.getIncidenceMatrix());
    System.out.println(p.reversed().getLiveness());
    System.out.println(p.reversed().getCoverabilityGraph());
    // System.out.println(pnUe43a.getCoverabilityGraph());
  }

}
