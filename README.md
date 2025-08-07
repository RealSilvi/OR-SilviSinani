# OR-SilviSinani

This repository is a university project.<br>
Course: Operative Research.<br>
University: L'Alma Mater Studiorum - Università di Bologna<br>

- The project represents the implementation of Branch and Bound Algorithm for solving PLI problems.<br>
- The IBM ILOG CPLEX library is used to solve the PL problems during the execution.<br>
- A basic ui shows six examples.<br>


## Recursive logic.
It works recursively as follows:
1) Solves the current problem.
2) It stops if :
- the solution is integer and is better than current best bound.
- all solutions are impossible.
3) The recursive pass consists on creating new problems from the current by adding cuts
that delete the current not integer solution.
And then calls itself to the new problems.


<b>Note:</b> 
- The next decision variable 'x' to cut is chose by which(k = x % 1) is closer to 0.5.
- The next problem to solve is chose by best bound first.
- The algorithm stores the problems in a binary tree.
    

## Instructions
<b>Note:</b> You must have access to <a href="https://www.ibm.com/products/ilog-cplex-optimization-studio">IBM CPLEX</a> by yourself.
- Copy the repository on your locale.
- Link the IBM CPLEX library to the project and create an appropriate run configuration. <a href="https://www.ibm.com/docs/en/icos/22.1.1?topic=cplex-setting-up-eclipse-java-api">See how</a>
- Run the project.

## Developers

Silvi Sinani - silvi.sinani@studio.unibo.it
