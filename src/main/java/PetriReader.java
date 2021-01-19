import exception.NotExistingNodeException;
import exception.WrongDimensionException;
import graphs.CoverabilityGraph;
import graphs.Petrinet;
import graphs.PetrinetWithCapacity;
import graphs.objects.Marking;
import graphs.objects.edges.Edge;
import graphs.objects.nodes.Place;
import graphs.objects.nodes.Transition;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Class to read a petrinet from the command line. Parses a pn-string into lists and creates
 * petrinet.
 */
public class PetriReader {

  private static Petrinet petrinet;

  private static boolean humanReadable;
  private static boolean printPetriNet;
  private static boolean printCoverabilityGraph;
  private static String nameCG;
  private static String namePN;
  private static String pnString;
  private static String markingsString;

  /**
   * Main method of petri-reader.
   *
   * @param args default arguments
   */
  public static void main(String[] args) {
    humanReadable = false;
    printPetriNet = true;
    printCoverabilityGraph = false;
    namePN = "petrinet";
    nameCG = "ueb";
    pnString = "";
    markingsString = "";
    for (String arg : args) {
      readArguments(arg);
    }
    if (pnString.equals("") || markingsString.equals("")) {
      System.out.println("Please add your pn-string or add -h for more help.");
      System.exit(0);
    }

    petrinet = createPetriNetAndMarkings(pnString, markingsString);

    CoverabilityGraph coverabilityGraph = new CoverabilityGraph(petrinet.getMue0(), nameCG,
        petrinet);

    //print graphs to commandline
    if (printPetriNet) {
      System.out.print(petrinet.toString());
    }
    if (printCoverabilityGraph) {
      System.out.println(coverabilityGraph.toString());
    }
  }

  /**
   * Read configuration and data for the petri-net.
   */
  private static void readArguments(String arg) {
    if (arg.equals("-h") || arg.equals("--help")) {
      printHelp();
    } else if (arg.equals("-r") || arg.equals("--readable")) {
      humanReadable = true;
    } else if (arg.startsWith("--net")) {
      namePN = arg.substring(5);
    } else if (arg.startsWith("--cover")) {
      nameCG = arg.substring(7);
    } else if (arg.equals("--printcover")) {
      printPetriNet = false;
      printCoverabilityGraph = true;
    } else if (arg.equals("--printboth")) {
      printCoverabilityGraph = true;
    } else if (arg.startsWith("-p")) {
      pnString = arg.substring(2);
    } else if (arg.startsWith("-m")) {
      markingsString = arg.substring(2);
    }
  }

  /**
   * Creates a petrinet with given strings.
   *
   * @param pnString       string for the petri-net
   * @param markingsString string for the initial mark
   * @return the constructed petri-net
   */
  public static Petrinet createPetriNetAndMarkings(String pnString, String markingsString) {
    Marking mue0 = new Marking(markingsString.split(","));
    List<Place> places = new ArrayList<>();
    List<Transition> transitions = new ArrayList<>();
    List<Edge> flow = new ArrayList<>();

    String[] pnStringParts = pnString.split(";;");
    if (pnStringParts.length == 2) {
      Arrays.stream(pnStringParts[0].split(";"))
          .map(x -> x.split(":"))
          .map(PetriReader::createPlaceNodes)
          .forEach(entry -> {
            places.add(entry.getKey());
            flow.addAll(entry.getValue());
          });
      Arrays.stream(pnStringParts[1].split(";"))
          .map(x -> x.split(":"))
          .map(PetriReader::createTransitionNodes)
          .forEach(entry -> {
            transitions.add(entry.getKey());
            flow.addAll(entry.getValue());
          });
    } else {
      throw new IllegalArgumentException("The pn-string needs the right format.");
    }
    return new Petrinet(namePN, places, transitions, flow, mue0);
  }

  /**
   * Adds place nodes to the petrinet from pn-string-split.
   *
   * @param split split of transition part of pn-string with two parts. First is the obligatory name
   *              of the place and second the optional list of places cut by commas.
   * @throws NotExistingNodeException throws a runtime exception, in case the node is invalid.
   */
  private static Entry<Place, List<Edge>> createPlaceNodes(String[] split) {
    if (split.length == 0 || split[0].equals("")) {
      throw new NotExistingNodeException("Place must be present and have a name.");
    }
    Place place = new Place(split[0]);
    if (split.length > 1 && !split[1].equals("")) {
      String[] toNodes = split[1].split(",");
      if (toNodes.length > 0) {
        return new SimpleEntry<>(place, Arrays.stream(toNodes)
            .map(t -> new Edge<>(place, new Transition(t)))
            .collect(Collectors.toList()));
      }
    }
    return new SimpleEntry<>(place, Collections.emptyList());
  }

  /**
   * Adds transition nodes to the petrinet.
   *
   * @param split split of place part of pn-string with two parts. First is the obligatory name of
   *              the transition, which should exist and second the optional list of places cut by
   *              commas.
   * @throws NotExistingNodeException throws a runtime exception, in case the node does not exist
   */
  public static Entry<Transition, List<Edge>> createTransitionNodes(String[] split)
      throws NotExistingNodeException {
    if (split.length == 0 || split[0].equals("")) {
      throw new NotExistingNodeException("Transition must be present and have a name.");
    }
    Transition transition = new Transition(split[0]);
    if (split.length > 1 && !split[1].equals("")) {
      String[] toNodes = split[1].split(",");
      if (toNodes.length > 0) {
        return new SimpleEntry<>(transition, Arrays.stream(toNodes)
            .map(p -> new Edge<>(transition, new Place(p)))
            .collect(Collectors.toList()));
      }
    }
    return new SimpleEntry<>(transition, Collections.emptyList());
  }

  /**
   * Creates a petri-net with given Strings.
   *
   * @param pnString       string for the petri-net
   * @param markingsString string for the initial mark
   * @param capacity       vector with capacity of each place
   * @return the constructed petri-net
   */
  public static PetrinetWithCapacity createPetriNetWithCapacityAndMarkings(String pnString,
      String markingsString, Marking capacity) {
    //TODO: Auslagern, überlappung mit normalem petrinetz
    Marking mue0 = new Marking(markingsString.split(","));
    List<Place> places = new ArrayList<>();
    List<Transition> transitions = new ArrayList<>();
    List<Edge> flow = new ArrayList<>();

    String[] pnStringParts = pnString.split(";;");
    if (pnStringParts.length == 2) {
      new Thread(() ->
          Arrays.stream(pnStringParts[0].split(";"))
              .map(x -> x.split(":"))
              .map(PetriReader::createPlaceNodes)
              .forEach(entry -> {
                places.add(entry.getKey());
                flow.addAll(entry.getValue());
              })).start();
      new Thread(() ->
          Arrays.stream(pnStringParts[1].split(";"))
              .map(x -> x.split(":"))
              .map(PetriReader::createTransitionNodes)
              .forEach(entry -> {
                transitions.add(entry.getKey());
                flow.addAll(entry.getValue());
              })).start();
    } else {
      throw new IllegalArgumentException("The pn-string needs the right format.");
    }

    if (capacity.getLength() == places.size()) {
      for (int i = 0; i < capacity.getLength(); i++) {
        places.get(i).setCapacity(capacity.get(i));
      }
      return new PetrinetWithCapacity("petrinet", places, transitions, flow, mue0, capacity);
    } else {
      throw new WrongDimensionException(
          "The capacity needs to be the same size as number of places in the petrinet.");
    }

  }

  /**
   * Prints a help string to the command-line.
   */
  private static void printHelp() {
    System.out.println(" Input of a pn-string and a marking:");
    //TODO: change to java lol
    System.out.println(
        " Call with: 'python pn_string_converter.py -p\"s1:t1,t2;s2:;;t1:s1;t2:s2;;\" -m\"3,0\"'");
    System.out.println(" To change the filename of the petrinet type e.g.: '--net\"petri_net\"'");
    System.out.println(
        " To change the filename of the coverability-graph type e.g.: '--cover\"ueb_graph\"'");
    System.out.println(" The program automatically prints the gv-string of the petrinet.");
    System.out.println(
        " If wished otherwise you can print the coverability-graph with: '--covprint' or make the "
            + "sets readable with '-r' or '--readable'.");
    System.exit(0);
  }

}
