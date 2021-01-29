import graphs.Petrinet;

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
            "s1:t3;s2:t4;s3:t5;s4:t1,t4;s5:t2,t4;s6:t3;s7:t6,t8;s8:t7,t9;s9:t5;;t1:s1,s2;t2:"
                + "s2,s3;t3:s4;t4:s7,s8;t5:s5;t6:s4;t7:s5;t8:s4,s6;t9:s5,s9;;",
            "0,0,0,0,0,1,1,1,1");
    p.getSimpleNet().ifPresent(s -> {
      System.out.println(s.getMinimalSiphons().toString());
      System.out.println(s.getLiveness());
    });
  }

}
