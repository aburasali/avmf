package org.avmframework.examples.allzeros;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.avmframework.AVM;
import org.avmframework.Monitor;
import org.avmframework.TerminationPolicy;
import org.avmframework.Vector;
import org.avmframework.initialization.RandomInitializer;
import org.avmframework.localsearch.GeometricSearch;
import org.avmframework.localsearch.LocalSearch;
import org.avmframework.objective.NumericObjectiveValue;
import org.avmframework.objective.ObjectiveFunction;
import org.avmframework.objective.ObjectiveValue;
import org.avmframework.variable.IntegerVariable;
import org.avmframework.variable.Variable;

public class AllZeros {

    static final int NUM_VARS = 10;
    static final int INIT = 0, MIN = -1000000, MAX = 1000000;
    static final int MAX_EVALUATIONS = 1000;

    static final LocalSearch LOCAL_SEARCH = new GeometricSearch();
    static final TerminationPolicy TERMINATION_POLICY = TerminationPolicy.maxEvaluations(MAX_EVALUATIONS);

    public static void main(String[] args) {

        // define the objective function
        ObjectiveFunction objFun = new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int distance = 0;
                for (Variable var : vector.getVariables()) {
                    distance += Math.abs(((IntegerVariable) var).getValue());
                }
                return NumericObjectiveValue.LowerIsBetterObjectiveValue(distance, 0);
            }
        };

        // set up the vector to be optimized
        Vector vector = new Vector();
        for (int i=0; i < NUM_VARS; i++) {
            vector.addVariable(new IntegerVariable(INIT, MIN, MAX));
        }

        // set up the random generator
        RandomGenerator rg = new MersenneTwister();

        // set up the initializer
        RandomInitializer ri = new RandomInitializer(rg);

        // set up the AVM
        AVM avm = new AVM(LOCAL_SEARCH, TERMINATION_POLICY, ri);

        // perform the search
        Monitor monitor = avm.search(vector, objFun);

        // output the results
        System.out.println("Best solution: " + monitor.getBestVector());
        System.out.println("Best objective value: " + monitor.getBestObjVal());
        System.out.println("Number of objective function evaluations: " + monitor.getNumEvaluations());
    }
}