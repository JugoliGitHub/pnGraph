package graphs;

import graphs.objects.Marking;
import graphs.objects.edges.Edge;
import graphs.objects.nodes.Place;
import graphs.objects.nodes.Transition;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TerminalPetrinet extends Petrinet implements Terminal {

  private Set<Marking> terminalMarkings;

  /**
   * Creates an empty petrinet. Should initialize mue0, capacity (if present) and places, transitions
   * and flow.
   *
   * @param name        name of the net
   * @param places
   * @param transitions
   * @param flow
   * @param mue0
   */
  public TerminalPetrinet(String name, List<Place> places,
      List<Transition> transitions,
      List<Edge> flow, Set<Marking> terminalMarkings, Marking mue0) {
    super(name, places, transitions, flow, mue0);
    this.terminalMarkings = terminalMarkings;
  }

  @Override
  public Set<Marking> getTerminalMarkings() {
    return terminalMarkings;
  }

  public TerminalPetrinet getNormalForm() {
    List<Place> placesIO = new ArrayList<>(places);
    Place sIn = new Place("sIn");
    Place sOut = new Place("sOut");
    Place sRun = new Place("sRun");
    placesIO.addAll(List.of(sIn, sOut, sRun));

    Marking mue0io = new Marking(placesIO.size()).add(places.size(), 1);
    Marking mueTio = new Marking(placesIO.size()).add(places.indexOf(sOut), 1);

    //ACHTUNG: wenn epsilon in sprache ist terminalMarkingsio list aus mueTio UND mue0io.
    Set<Marking> terminalMarkingsio = Set.of(mueTio);

    List<Transition> transitionsio = new ArrayList<>(transitions);
    List<Edge> flowio = new ArrayList<>(flow);
    transitionsio.forEach(
        transition -> flowio
            .addAll(List.of(new Edge(transition, sRun), new Edge(sRun, transition))));
    List<Transition> transitionsStart = transitionsio.stream()
        .filter(transition -> transition.getPreSet().lessEquals(mue0))
        .map(transition -> {
          Transition newTransition = new Transition(transition.getLabel() + "'");

          flowio.addAll(List.of(new Edge(sIn, newTransition), new Edge(newTransition, sRun)));
          IntStream.range(0, places.size())
              .filter(i -> mue0.sub(transition.getPreSet()).add(transition.getPostSet()).get(i) > 0)
              .forEach(i -> flowio.add(new Edge(newTransition, places.get(i))));

          return newTransition;
        })
        .collect(Collectors.toList());

    List<Transition> transitionsEnd = transitionsio.stream()
        .filter(
            transition -> terminalMarkings.stream().anyMatch(transition.getPostSet()::lessEquals))
        .map(
            transition ->
                new SimpleEntry<Transition, Marking>(transition,
                    terminalMarkings.stream().filter(transition.getPostSet()::lessEquals)
                        .findFirst().get()))
        .map(entry -> {
          Transition newTransition = new Transition(entry.getKey().getLabel() + "''");

          flowio.addAll(List.of(new Edge(sRun, newTransition), new Edge(newTransition, sOut)));
          IntStream.range(0, places.size())
              .filter(i ->
                  entry.getValue().sub(entry.getKey().getPostSet()).add(entry.getKey().getPreSet())
                      .get(i) > 0)
              .forEach(i -> flowio.add(new Edge(places.get(i), newTransition)));

          return newTransition;
        })
        .collect(Collectors.toList());
    transitionsio.addAll(transitionsStart);
    transitionsio.addAll(transitionsEnd);
    return new TerminalPetrinet(name + "Normal", placesIO, transitionsio, flowio,
        terminalMarkingsio, mue0io);
  }

}
