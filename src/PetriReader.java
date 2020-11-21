import exception.NotExistingNodeException;
import graph.Vector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PetriReader {

  private static Petrinet petrinet;

  private static boolean humanReadable, printPetriNet;
  private static String filenameCG, filenamePN;

  private static List<Vector> globalVisited;

  public static void main(String[] args) throws NotExistingNodeException {
    humanReadable = false;
    printPetriNet = true;
    filenamePN = "petrinet.gv";
    filenameCG = "ueb.gv";
    String pnString = "";
    String markingsString = "";
    for (String arg : args) {
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
      } else if (arg.startsWith("-p")) {
        pnString = arg.substring(2);
      } else if (arg.startsWith("-m")) {
        markingsString = arg.substring(2);
      }
    }
    if (pnString.equals("") || markingsString.equals("")) {
      System.out.println(" Please add your pn-string or add -h for more help.");
      System.exit(0);
    }
    petrinet = new Petrinet(filenamePN.substring(0, filenamePN.length() - 3));

    readPnString(pnString, markingsString);

    // petrinet.setVectors();
    // String coverabilityGraph =
    if (printPetriNet) {
      System.out.print(petrinet.toString());
    } else if (!printPetriNet) {
      System.out.println("");
    } else {
      System.exit(0);
    }
  }

  private static void readPnString(String pnString, String markingsString)
      throws NotExistingNodeException {
    Vector mue_0 = new Vector(markingsString.split(","));
    String[] pnStringParts = pnString.split(";;");
    Arrays.stream(pnStringParts[0].split(";")).map(x -> x.split(":"))
        .forEach(petrinet::addPlaceNodes);
    Arrays.stream(pnStringParts[1].split(";")).map(x -> x.split(":"))
        .forEach(petrinet::addTransitionNodes);
    petrinet.setMue0(mue_0);
  }

  private static void printHelp() {
    System.out.println(" Input of a pn-string and a marking:");
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

  private static CoverabilityGraph createCoverabilityGraph(Petrinet petrinet, String name) {
    CoverabilityGraph ueb = new CoverabilityGraph(petrinet.getMue0(), name, petrinet);
    return ueb;
  }
}
