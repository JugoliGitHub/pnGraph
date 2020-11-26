import exception.WrongDimensionException;
import graph.CoverabilityGraphEdge;
import graph.Edge;
import graph.Place;
import graph.Transition;
import graph.Vector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CoverabilityGraph {

  private String name;
  private Vector mue0;
  private List<Vector> markings;
  private List<CoverabilityGraphEdge> knots;

  private List<Vector> visited;
  private Petrinet petrinet;

  public CoverabilityGraph(Vector mue0, String name, Petrinet petrinet)
      throws WrongDimensionException {
    this.name = name;
    this.mue0 = mue0;
    this.markings = new ArrayList<>();
    this.knots = new ArrayList<>();
    markings.add(this.mue0);

    this.visited = new ArrayList<>();
    this.petrinet = petrinet;
    ArrayList<Vector> path = new ArrayList<>();
    path.add(mue0);
    go(mue0, path);
  }

  public boolean addToMarkings(Vector mark) {
    Optional<Vector> opt = markings.stream().filter(elem -> elem.equals(mark)).findFirst();
    return opt.isPresent() ? false : markings.add(mark);
  }

  public boolean addToKnots(CoverabilityGraphEdge edge) {
    Optional<CoverabilityGraphEdge> opt = knots.stream().filter(elem -> elem.equals(edge))
        .findFirst();
    return opt.isPresent() ? false : knots.add(edge);
  }

  private boolean addToVisited(Vector newVector) {
    Optional<Vector> opt = visited.stream().filter(elem -> elem.equals(newVector)).findFirst();
    return opt.isPresent() ? false : visited.add(newVector);
  }

  private Optional<Vector> fire(Vector mue, Transition transition) throws WrongDimensionException {
    // if: calculate post and pre vectors in method, else with vectors
    if (transition.getPostVector().getLength() == 0 || transition.getPreVector().getLength() == 0) {
      Vector newMue = new Vector(mue.getLength());
      newMue.add(mue);
      if (petrinet.getTransitions().contains(transition)) {
        List<Place> front_places = new ArrayList<>();
        List<Place> end_places = new ArrayList<>();

        for (Edge edge : petrinet.getFlow()) {
          if (edge.getFrom().equals(transition) && edge.getTo() instanceof Place) {
            end_places.add((Place) edge.getTo());
          } else if (edge.getTo().equals(transition) && edge.getFrom() instanceof Place) {
            front_places.add((Place) edge.getFrom());
          }
        }

        for (Place place : front_places) {
          int index = petrinet.getPlaces().indexOf(place);
          if (newMue.get(index) == 0) {
            return Optional.empty();
          } else if (newMue.get(index) >= 1) {
            if (!newMue.subAtIndex(index, 1)) {
              return Optional.empty();
            }
          }
        }
        for (Place place : end_places) {
          int index = petrinet.getPlaces().indexOf(place);
          newMue.addAtIndex(index, 1);
        }
        return Optional.of(newMue);
      }
    } else {
      //TODO: implement
    }
    return Optional.empty();
  }

  private void go(Vector mue, List<Vector> path) throws WrongDimensionException {
    for (Transition transition : petrinet.getTransitions()) {
      Optional<Vector> newMueOptional = fire(mue, transition);
      if (newMueOptional.isPresent()) {
        Vector newMue = newMueOptional.get();
        setOmega(newMue, path);
        addToMarkings(newMue);
        addToKnots(new CoverabilityGraphEdge(mue, transition, newMue));
        if (addToVisited(newMue)) {
          path.add(newMue);
          go(newMue, path);
          path.remove(path.size() - 1);
        }
      }
    }
  }

  private void setOmega(Vector mue, List<Vector> path) {
    List<Place> places = petrinet.getPlaces();
    boolean[] omegas = new boolean[petrinet.getPlaces().size()];
    boolean[] omegaKand = new boolean[petrinet.getPlaces().size()];
    for(int i = path.size() - 1; i >= 0; i--) {
      Vector knot = path.get(i);
      if(knot.lessThan(mue)) {
        for(int s = 0; s < places.size(); s++) {
          if(knot.get(s) < mue.get(s) && mue.get(s) != -1) omegaKand[s] = true;
        }
      }
      for(int s = 0; s < places.size(); s++){
        if(omegaKand[s]) omegas[s] = true;
      }
      omegaKand = new boolean[petrinet.getPlaces().size()];
    }
    for(int s = 0; s < places.size(); s++) {
      if(omegas[s]) mue.setOmega(s);
    }
  }

  @Override
  public String toString() {
    StringBuilder out = new StringBuilder(
        "digraph " + name + "{\nrankdir=\"LR\";\nnode[shape=plaintext];\n");
    knots.forEach(edge -> out.append(edge.toString()));
    out.append("}\n");
    return out.append("\n").toString();
  }
}
