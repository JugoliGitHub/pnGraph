import exception.NotExistingNodeException;
import exception.WrongDimensionException;
import graph.Place;
import graph.Vector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class to read a Petrinet from the command line.
 */
public class PetriReader {

  private static Petrinet petrinet;

  private static boolean humanReadable, printPetriNet, printCoverabilityGraph;
  private static String filenameCG, filenamePN, pnString, markingsString, covString;

  public static void main(String[] args) throws NotExistingNodeException {
    humanReadable = false;
    printPetriNet = true;
    printCoverabilityGraph = false;
    filenamePN = "petrinet.gv";
    filenameCG = "ueb.gv";
    pnString = "";
    markingsString = "";
    covString = "";
    for (String arg : args) {
      readArguments(arg);
    }
    if (pnString.equals("") || markingsString.equals("")) {
      System.out.println(" Please add your pn-string or add -h for more help.");
      System.exit(0);
    }
    petrinet = new Petrinet(filenamePN.substring(0, filenamePN.length() - 3));
    readPnString(pnString, markingsString);

    CoverabilityGraph coverabilityGraph = new CoverabilityGraph(petrinet.getMue0(),
        filenameCG.substring(0, filenameCG.length() - 3), petrinet);
    covString = coverabilityGraph.toString();

    //print graphs to commandline
    if (printPetriNet) {
      System.out.print(petrinet.toString());
    }
    if (printCoverabilityGraph) {
      System.out.println(covString);
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
      filenamePN = arg.substring(5);
      if (!filenamePN.substring(filenamePN.length() - 3).equals(".gv")) {
        filenamePN += ".gv";
      }
    } else if (arg.startsWith("--cover")) {
      filenameCG = arg.substring(5);
      if (!filenameCG.substring(filenameCG.length() - 3).equals(".gv")) {
        filenameCG += ".gv";
      }
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
   * Creates a petrinet with given strings, when strings are correct.
   *
   * @param pnString       string for the petri-net
   * @param markingsString string for the initial mark
   * @return the constructed petri-net
   */
  public static Petrinet createPetriNetAndMarkings(String pnString, String markingsString) {
    Vector mue_0 = new Vector(markingsString.split(","));
    Petrinet pNet = new Petrinet("petrinet");
    String[] pnStringParts = pnString.split(";;");
    if (pnStringParts.length == 2) {
      Arrays.stream(pnStringParts[0].split(";")).map(x -> x.split(":"))
          .forEach(pNet::addPlaceNodes);
      Arrays.stream(pnStringParts[1].split(";")).map(x -> x.split(":"))
          .forEach(pNet::addTransitionNodes);
    } else {
      throw new IllegalArgumentException("The pn-string needs the right format.");
    }
    if (mue_0.getLength() == pNet.getPlaces().size()) {
      pNet.setMue0(mue_0);
      pNet.setVectors();
      pNet.setInitialBoundedness();
    } else {
      throw new WrongDimensionException(
          "The markings-string needs the same size as number of places in the petrinet.");
    }
    return pNet;
  }

  /**
   * Creates a petri-net with given Strings.
   *
   * @param pnString       string for the petri-net
   * @param markingsString string for the initial mark
   * @param capacity       vector with capacity of each place
   * @return the constructed petri-net
   */
  public static Petrinet createPetriNetAndMarkings(String pnString, String markingsString,
      Vector capacity) {
    Petrinet pNet = createPetriNetAndMarkings(pnString, markingsString);
    pNet.setCapacity(capacity);
    List<Place> places = pNet.getPlaces();
    if (capacity.getLength() == places.size()) {
      for (int i = 0; i < capacity.getLength(); i++) {
        places.get(i).setCapacity(capacity.get(i));
      }
    } else {
      throw new WrongDimensionException(
          "The capacity needs to be the same size as number of places in the petri-net.");
    }
    return pNet;
  }

  /**
   * Sets a constructed petri-net as the global net.
   *
   * @param pnString       string for the petri-net
   * @param markingsString string for the initial mark
   */
  private static void readPnString(String pnString, String markingsString) {
    petrinet = createPetriNetAndMarkings(pnString, markingsString);
  }

  /**
   * Prints a help string to the command-line.
   */
  private static void printHelp() {
    System.out.println(" Input of a pn-string and a marking:");
    //TODO: change to java
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
