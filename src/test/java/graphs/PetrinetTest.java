package graphs;//import static org.hamcrest.MatcherAssert.assertThat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import graphs.objects.Vector;
import graphs.objects.edges.Edge;
import graphs.objects.nodes.Place;
import graphs.objects.nodes.Transition;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * Class to test the petrinet and its subclasses.
 */
public class PetrinetTest {

  @Mock
  Place place1;
  @Mock
  Transition transition1;
  @Mock
  Vector mue0;
  List<Place> places;
  List<Transition> transitions;
  List<Edge> flow;

  Petrinet pn;

  @BeforeEach
  void setUp() {
    places = new ArrayList<>();
    transitions = new ArrayList<>();
    place1 = Mockito.mock(Place.class);
    transition1 = Mockito.mock(Transition.class);
    places.add(place1);
    transitions.add(transition1);
    mue0 = Mockito.mock(Vector.class);
    flow = new ArrayList<>();
    flow.add(new Edge(place1, transition1));
  }

  @Test
  void createNetWithWrongMue0ThrowsException() {
    Mockito.when(mue0.getLength()).thenReturn(0);

    try {
      pn = new Petrinet("petrinet", places, transitions, flow, mue0);
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "This is no valid petrinet");
    }
  }

  @Test
  void createNetWithNoTransitionsThrowsException() {
    Mockito.when(mue0.getLength()).thenReturn(1);
    transitions.remove(transition1);

    //check other options, so only the one is not correct
    assertEquals(mue0.getLength(), places.size());

    try {
      pn = new Petrinet("petrinet", places, transitions, flow, mue0);
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "This is no valid petrinet");
    }
  }

  @Test
  void createNetWithNoConnectionThrowsException() {
    Mockito.when(mue0.getLength()).thenReturn(1);
    flow.remove(0);

    //check other options, so only the one is not correct
    assertTrue(transitions.size() > 0 && mue0.getLength() == places.size());

    try {
      pn = new Petrinet("petrinet", places, transitions, flow, mue0);
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "This is no valid petrinet");
    }
  }


  @Test
  void createCorrectPetrinetWorks() {
    //TODO: why does it not work with mocks???
    place1 = new Place("s1");
    transition1 = new Transition("t1");
    places = List.of(place1);
    transitions = List.of(transition1);
    mue0 = new Vector(new int[]{1});
    flow = List.of(new Edge(place1, transition1));

    pn = new Petrinet("petrinet", places, transitions, flow, mue0);

    assertEquals(pn.getMue0(), mue0);
    assertEquals(pn.getPlaces().get(0), place1);
    assertEquals(pn.getTransitions().get(0), transition1);
    assertEquals(pn.getBoundedness(), 1);
    assertEquals(pn.getLiveness(), -1);
    /*// set vectors check
    Mockito.verify(place1, Mockito.times(1)).setVectors(Mockito.any(), Mockito.any(),
        Mockito.any(Integer.class));
    Mockito.verify(transition1, Mockito.times(1)).setVectors(Mockito.any(), Mockito.any(),
        Mockito.any(Integer.class));

    // set initial boundedness check
    Mockito.verify(place1, Mockito.times(1)).setBoundedness(Mockito.anyInt()); */
  }

  @Test
  void createReversedPetrinetWorks() {
    place1 = new Place("s1");
    transition1 = new Transition("t1");
    places = List.of(place1);
    transitions = List.of(transition1);
    mue0 = new Vector(new int[]{1});
    flow = List.of(new Edge(place1, transition1));

    pn = new Petrinet("petrinet", places, transitions, flow, mue0).reversed();

    assertEquals(pn.getMue0(), mue0);
    assertEquals(pn.getPlaces().get(0), place1);
    assertEquals(pn.getTransitions().get(0), transition1);
    assertEquals(pn.getBoundedness(), 1);
    assertEquals(pn.getLiveness(), -1);
    assertEquals(pn.getFlow().size(), 1);
    assertEquals(pn.getFlow().get(0), new Edge<>(transition1, place1));
  }

  @Test
  void createDualPetrinetWorks() {

  }

}
