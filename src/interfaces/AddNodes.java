package interfaces;

import exception.NotExistingNodeException;

/** Very unpleasant mistreatment of an interface. */
public interface AddNodes {

  /**
   * Should be functional, but isn´t for the beauty of lambda.
   * Adds transition nodes to the petri-net which implements this interface.
   * @param split split of transition part of pn-string
   * @throws NotExistingNodeException throws a runtime exception, in case the node does not exist
   */
  public void addTransitionNodes(String[] split) throws NotExistingNodeException;

  /**
   * Should be functional, but isn´t for the beauty of lambda.
   * Adds transition nodes to the petri-net which implements this interface.
   * @param split split of place part of pn-string.
   * @throws NotExistingNodeException throws a runtime exception, in case the node does not exist
   */
  public void addPlaceNodes(String[] split) throws NotExistingNodeException;

}
