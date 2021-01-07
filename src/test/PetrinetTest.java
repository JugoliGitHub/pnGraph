//import static org.hamcrest.MatcherAssert.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import main.Petrinet;
import main.exception.NotExistingNodeException;
import main.graph.Place;
import main.graph.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class PetrinetTest {

  @Mock Vector mue0;
  @Mock Place place1;
  @Mock Place place2;
  Petrinet pn;

  @BeforeEach
  public void setUp() {
    pn = new Petrinet("petrinet");
    pn.setMue0(mue0);
  }

  @Test
  public void createNetTest() {
    Petrinet petri = new Petrinet("Petri-Net-1");

    assertEquals("digraph Petri-Net-1{\nnode[shape=circle];\n}\n", petri.toString());
    assertTrue(petri.getPlaces().isEmpty());
    assertTrue(petri.getTransitions().isEmpty());
    assertTrue(petri.getFlow().isEmpty());
    assertEquals(petri.getMue0().getLength(), 0);
    assertEquals(petri.getCapacity().getLength(), 0);

    assertTrue(pn.getPlaces().isEmpty());
    assertTrue(pn.getTransitions().isEmpty());
    assertTrue(pn.getFlow().isEmpty());
    assertEquals(mue0, pn.getMue0());
    assertEquals(0, pn.getCapacity().getLength());
  }

  @Test
  public void addPlaceNodesTest() {
    assertTrue(pn.getPlaces().isEmpty());
    pn.addPlaceNodes(new String[]{"p1"});
    assertEquals("p1", pn.getPlaces().get(0).toString());

    try {
      pn.addPlaceNodes(new String[]{""});
    } catch(RuntimeException re) {
      assertEquals("Place must be present and have a name.", re.getMessage());
    }

    pn.addPlaceNodes(new String[]{"p2", "t1,t2"});
    assertEquals(2, pn.getPlaces().size());
    assertEquals(2, pn.getFlow().size());

    pn.addPlaceNodes(new String[]{"p3", ""});
    assertEquals(3, pn.getPlaces().size());
    assertEquals(2, pn.getFlow().size());
  }

  @Test
  public void addTransitionNodesTest() throws NotExistingNodeException{
    assertTrue(pn.getTransitions().isEmpty());
    pn.addTransitionNodes(new String[]{"t1"});
    assertEquals(pn.getTransitions().get(0).toString(), "t1");

    assertThrows(NotExistingNodeException.class, () -> pn.addTransitionNodes(new String[]{"t1", "p1"}));

    pn.addPlace(place1);
    pn.addPlace(place2);
    //why null pointer??
    when(place1.toString()).thenReturn("p1");
    when(place2.toString()).thenReturn("p2");
    pn.addTransitionNodes(new String[]{"t1", "p1,p2"});
    assertEquals("p2", pn.getTransitions().get(1).toString());
    assertEquals(2, pn.getFlow().size());

    pn.addPlaceNodes(new String[]{"p3", ""});
    assertEquals("p3", pn.getPlaces().get(2).toString());
    assertEquals(2, pn.getFlow().size());
  }

}
