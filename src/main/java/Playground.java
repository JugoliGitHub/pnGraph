import graphs.PetrinetWithCapacity;
import graphs.objects.Marking;
import graphs.objects.edges.Edge;
import graphs.objects.nodes.Place;
import graphs.objects.nodes.Transition;
import java.util.List;

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
    /*//patrinet.Petrinet petrinet = patrinet.PetriReader.createPetriNetAndMarkings(
    "s1:t3,t4;s2:t3;s3:t4;s4:t1,t2;s5:;;" +
    // "t1:s1,s5;t2:s1;t3:s3,s4;t4:s2,s4;;", "1,1,0,0,0");
    patrinet.Petrinet petrinet = patrinet.PetriReader
    .createPetriNetAndMarkings("s1:t1;s2:t2,t3;;t1:s2;t2:;t3:s1;;", "1,1",
    new Vector(new int[]{2, 1}));

    // patrinet.Petrinet petrinet = patrinet.PetriReader
    //   .createPetriNetAndMarkings("s1:t1,t4;s2:t2,t3,t4;;t1:s2;t2:s1;t3:s1,s2;t4:;;", "1,0");
    patrinet.LabelledTransitionSystem covGraph = new patrinet.LabelledTransitionSystem(
    petrinet.getMue0(), "covGraph", petrinet);
    System.out.println(petrinet);
    System.out.println(covGraph);
    System.out
        .println("Bounded: " + (petrinet.getBoundedness() == -1 ? "ω" : petrinet.getBoundedness()));
    System.out.println("Liveness: " + petrinet.getLiveness());

    Edge<Place, Transition> edge = new Edge<>(new Place("s1"), new Transition("t1"));
    Edge<Transition, Place> edge2 = edge.reverse();
    List<Edge> fl = List.of(edge, edge2);
    List<Edge> flow = fl.stream().map(Edge::reverse).collect(Collectors.toList());
    System.out.println(flow.toString());
    System.out.println(fl.toString()); */
    //Petrinet p = PetriReader
    //  .createPetriNetAndMarkings("s1:a,b;s2:c;s3:d;s4:;;a:s2;b:s3;c:s2,s3;d:s2,s4;;", "1,0,0,0");
    //.createPetriNetAndMarkings("s1:a;s2:b;;a:s2;b:s1;;", "1,0");
    //.createPetriNetAndMarkings("s1:a;s2:b;s3:c;;a:s2;b:s3;c:s1;;", "1,0,0");
    //.createPetriNetAndMarkings("s1:a;;a:s1;;", "1");
    // .createPetriNetAndMarkings(
    // "s1:t1;s2:t1,t1,t2;s3:t3;s4:t4,t4,t5;;t1:s2,s2;t2:s3;t3:s4;t4:s1,s4,s4;t5:s1,s2,s2;;",
    // "0,0,0,1");
    //.createPetriNetAndMarkings(
    //"s1:a,b;s2:c;s3:d;;a:s2;b:s3;c:s2,s3;d:s2;;",
    //"1,0,0");
    // System.out.println(p.isStronglyConnected());
    //System.out.println(p.toString());
    //System.out.println(p.isStronglyConnected());
    //System.out.println(p.containsLoop());

    /*Petrinet p = PetriReader
        .createPetriNetAndMarkings(
            ""s1:t3;s2:t4;s3:t5;s4:t1,t4;s5:t2,t4;s6:t3;s7:t6,t8;s8:t7,t9;s9:t5;;
t1:s1,s2;t2:s2,s3;t3:s4;t4:s7,s8;t5:s5;t6:s4;t7:s5;t8:s4,s6;t9:s5,s9;;",
            "2,1,0,0,1");

    System.out.println(p.getTransitionInvariants().toString());*/

   // Petrinet p = PetriReader
     //   .createPetriNetAndMarkings(
       //     "s1:t3;s2:t4;s3:t5;s4:t1,t4;s5:t2,t4;s6:t3;s7:t6,t8;s8:t7,t9;s9:t5;;t1:s1,s2;t2:s2,s3;t3:s4;t4:s7,s8;t5:s5;t6:s4;t7:s5;t8:s4,s6;t9:s5,s9;;",
         //   "0,0,0,0,0,1,1,1,1");
    //System.out.println(p.getMinimalSiphons().toString());

    Place s1 = new Place("s1");
    Place s2 = new Place("s2");
    Transition t1 = new Transition("t1");
    Transition t2 = new Transition("t2");
    Transition t3 = new Transition("t3");
    PetrinetWithCapacity petrinetWithCapacity = new PetrinetWithCapacity("p",
        List.of(s1, s2),
        List.of(t1, t2, t3),
        List.of(new Edge(s1, t1), new Edge(t1, s2), new Edge(s2, t2), new Edge(s2, t3), new Edge(t3, s1)),
        new Marking(new int[]{1, 1}),
        new Marking(new int[]{2, 1}));
    System.out.println(petrinetWithCapacity.toString());
    System.out.println(petrinetWithCapacity.createCoverabilityGraph().toString());
    System.out.println(petrinetWithCapacity.getPetrinet().toString());
    System.out.println(petrinetWithCapacity.getPetrinet().getMue0().toString());
  }

}
