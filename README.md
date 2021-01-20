# pnGraph - Project to lecture

This is a repository about petrinets. Due to a lecture at university I created this project simultanious to my studies.
To create own petrinets we used pn-strings. These are used as inputs to create the petrinets.
The ```toString()```-methods create an output to display graphs with [graphViz](http://www.graphviz.org/). 
### pn-strings
Strings of the form: ```-p"s1:t1;;t1:s1;;" -m"1"```. Petrinets must be correct to be created. 
A correct petrinet
* has transitions
* is connected
* contains the same number of places as dimension of the marking.
#### p-part
In the section after -p the petrinet is defined.

```place1 : transitions; place2 : transitions ;; transition2 : places;;```

#### m-part
This section defines the markings. These are the number of tokens at each place.

```#ofTokens@place1 , #ofTokens@place2```

### Graphs
The output for the [graphViz](http://www.graphviz.org/)-Graphs will create circles for the places
and rectangles for transitions. The places can contain markings, which are shown as dots when the 
count does not exceed 6 tokens. Otherwise, the number of tokens is printed.

The output of coverability-graphs is shown as nodes with the markings string and at each edge is the 
used transition labelled.

# Features
## Create a Petrinet and Analyse a Petrinet
### petrinet
Petrinets are place/transition-systems. They are either created with the 
[PetriReader](/src/main/java/PetriReader.java) and a corresponding pn-string or with given constructor.
You need each a list of places, transitions and edges. When the net is correct you can analyse 
the following attributes:
* strongly connected (wip)
* contains loops
* boundedness
* todo add other: free choice s.o.

Additionally you can create the following graphs:
* dual petrinet
* reverse petrinet
* [coverabillity graph](src/main/java/graphs/CoverabilityGraph.java)
 
### with capacity
### condition/event system


### reachability graph
### coverability graph
### labelled transition system

# Use
## inside IDE 
The easiest way with the class [Playground](src/main/java/Playground.java).

## From the commandline

## final build
TODO: gui?
