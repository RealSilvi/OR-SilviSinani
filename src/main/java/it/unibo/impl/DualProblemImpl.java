package it.unibo.impl;

import ilog.concert.*;
import ilog.opl.IloCplex;

public class DualProblemImpl {

    private final IloCplex cplex = new IloCplex();

    public DualProblemImpl() throws IloException {
        this.cplex.setParam(IloCplex.Param.RootAlgorithm, IloCplex.Algorithm.Dual);
    }
}
