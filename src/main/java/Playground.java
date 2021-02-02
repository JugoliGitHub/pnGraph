import graphs.Petrinet;
import graphs.objects.Marking;
import java.util.Set;

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
    System.out.println(p.toString());
    p.getPureNet().ifPresent(s -> {
      System.out.println(p.toString());
      System.out.println(s.getMinimalSiphons().toString());
      System.out.println(s.getLiveness());
    });

    Petrinet pnUe43b = PetriReader.createPetriNetAndMarkings("ue43",
        "s1:t2;s2:t1;s3:t3;s4:t3;s5:t4;;t1:s1;t2:s2,s3;t3:s5;t4:s4;;", "1,0,0,1,0");
    Petrinet pnUe43a = PetriReader.createPetriNetAndMarkings("ue43",
        "s1:t2;s2:t1;s3:t3;s4:t3;s5:t4;s6:t2;;t1:s1;t2:s2,s3;t3:s5,s6;t4:s4;;", "1,0,0,1,0,3");
    System.out.println(pnUe43a.toString());
    System.out.println(pnUe43a.getNormalform(
        Set.of(new Marking(new int[]{0, 1, 0, 0, 1, 3}), new Marking(new int[]{1, 0, 0, 0, 1, 3})))
        .getNormalForm().toString());

    System.out.println(pnUe43a.getCoverabilityGraph());
  }

}
