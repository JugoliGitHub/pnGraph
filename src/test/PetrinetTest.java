//import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import main.Petrinet;
import main.graph.Edge;
import main.graph.Place;
import main.graph.Transition;
import main.graph.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class PetrinetTest {

  @Mock Vector mue0;
  @Mock Place place1;
  @Mock Place place2;
  @Mock List<Place> places;
  @Mock List<Transition> transitions;
  @Mock List<Edge> flow;
  Petrinet pn;

  @BeforeEach
  void setUp() {
    pn = new Petrinet("petrinet", places, transitions, flow, mue0);
  }

  @Test
  void createNetThrowsException() {
    // Nets with loops; transitions or places in edge that do not exist
  }

  @Test
  void createCorrectPetrinetWorks() {
    // initialize a correct petrinet
  }

}
