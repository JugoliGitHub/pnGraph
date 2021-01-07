# pnGraph

This is a repository about petrinets. Due to a lecture at university I created this project simultanious to my studies.
To create own petrinets we used pn-strings. These are used as inputs to create the petrinets.
The ```toString()```-methods create an output to display graphs with [graphViz](http://www.graphviz.org/). 
### pn-strings
Strings of the form: ```-p"s1:t1;;t1:s1;;" -m"1"```
#### p-part
In the section after -p the petrinet is defined.

place1 : transitions; place2 : transitions;; transition2 : places;;

# Features
## Create a Petrinet
### petrinet
### with capacity
### condition/event system

## Analyse a Petrinet
### reachability graph
### coverability graph
### labelled transition system

# Use
## inside IDE 
Easiest way with the class [Playground](src/main/java/Playground.java).

## From the commandline

## final build
TODO: gui?
