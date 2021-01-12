package graphs;//import static org.hamcrest.MatcherAssert.assertThat;

import static org.junit.Assert.assertEquals;

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

  List<Place> places;
  List<Transition> transitions;
  @Mock Place place1;
  @Mock Transition transition1;
  @Mock Vector mue0;
  @Mock List<Edge> flow;
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
    flow = Mockito.mock(List.class);
  }

  @Test
  void createNetThrowsException() {
    Mockito.when(mue0.getLength()).thenReturn(0);

    try {
      pn = new Petrinet("petrinet", places, transitions, flow, mue0);
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "This is no valid petrinet");
    }
  }

  @Test
  void createCorrectPetrinetWorks() {
    Mockito.doNothing().when(place1).setVectors(Mockito.isA(List.class), Mockito.isA(List.class),
        Mockito.isA(Integer.class));
    Mockito.doNothing().when(transition1).setVectors(Mockito.isA(List.class), Mockito.isA(List.class),
        Mockito.isA(Integer.class));
    Mockito.when(mue0.getLength()).thenReturn(1);

    pn = new Petrinet("petrinet", places, transitions, flow, mue0);

    // set vectors check
    Mockito.verify(place1, Mockito.times(1)).setVectors(Mockito.any(), Mockito.any(),
        Mockito.any(Integer.class));
    Mockito.verify(transition1, Mockito.times(1)).setVectors(Mockito.any(), Mockito.any(),
        Mockito.any(Integer.class));

    // set initial boundedness check
    Mockito.verify(place1, Mockito.times(1)).setBoundedness(Mockito.anyInt());
  }

}
