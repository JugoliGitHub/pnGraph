import graphs.LabelledTerminalPetrinet;
import graphs.Petrinet;
import graphs.objects.Marking;
import graphs.objects.edges.Edge;
import graphs.objects.nodes.Place;
import graphs.objects.nodes.Transition;
import java.util.List;
import java.util.Map;
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
    p.getSimpleNet().ifPresent(s -> {
      System.out.println(s.getMinimalSiphons().toString());
      System.out.println(s.getLiveness());
    });
    Transition t1 = new Transition("t1");
    Transition t2 = new Transition("t2");
    Transition t3 = new Transition("t3");
    Transition t4 = new Transition("t4");
    Place s1 = new Place("s1");
    Place s2 = new Place("s2");
    Place s3 = new Place("s3");
    Place s4 = new Place("s4");

    LabelledTerminalPetrinet ue13 = new LabelledTerminalPetrinet("terminalPetri",
        List.of(s1, s2, s3, s4),
        List.of(t1, t2, t3, t4),
        List.of(new Edge(s1, t1), new Edge(t1, s2), new Edge(s2, t2), new Edge(t2, s3),
            new Edge(s3, t3), new Edge(t3, s4), new Edge(s4, t4), new Edge(t4, s1)),
        Set.of(new Marking(new int[]{1, 0, 0, 0})),
        List.of("a", "b", "c", "d"),
        Map.of(t1, "a", t2, "b", t3, "c", t4, "d"),
        new Marking(new int[]{1, 0, 0, 0}));
    System.out.println(ue13.toString());
    System.out.println(ue13.getNormalForm().toString());
  }

}
