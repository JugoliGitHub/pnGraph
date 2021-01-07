package main;

import main.exception.WrongDimensionException;
import main.graph.Vector;

/**
 * This class is for quick testing inside my IDE.
 */
public class Playground {

  public static void main(String[] args) throws WrongDimensionException {
    /*//patrinet.Petrinet petrinet = patrinet.PetriReader.createPetriNetAndMarkings("s1:t3,t4;s2:t3;s3:t4;s4:t1,t2;s5:;;" +
    // "t1:s1,s5;t2:s1;t3:s3,s4;t4:s2,s4;;", "1,1,0,0,0");
    patrinet.Petrinet petrinet = patrinet.PetriReader.createPetriNetAndMarkings("s1:t1;s2:t2,t3;;t1:s2;t2:;t3:s1;;", "1,1",
        new Vector(new int[]{2, 1}));

    // patrinet.Petrinet petrinet = patrinet.PetriReader
    //   .createPetriNetAndMarkings("s1:t1,t4;s2:t2,t3,t4;;t1:s2;t2:s1;t3:s1,s2;t4:;;", "1,0");
    patrinet.LabelledTransitionSystem covGraph = new patrinet.LabelledTransitionSystem(petrinet.getMue0(), "covGraph", petrinet);
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

    Vector v1 = new Vector(new int[]{1,2});
    Vector v2 = new Vector(v1.getVectorArray());

    System.out.println(v1);
    System.out.println(v2);

    v1.addAtIndex(0, 1);

    System.out.println(v1);
    System.out.println(v2);
  }

}
