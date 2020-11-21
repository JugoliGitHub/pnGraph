package interfaces;

import exception.NotExistingNodeException;

public interface AddNodes {

  public void addTransitionNodes(String[] split) throws NotExistingNodeException;

  public void addPlaceNodes(String[] split) throws NotExistingNodeException;

}
