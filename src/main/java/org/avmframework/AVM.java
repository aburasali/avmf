package org.avmframework;

import org.avmframework.initialization.Initializer;
import org.avmframework.localsearch.LocalSearch;
import org.avmframework.objective.ObjectiveFunction;
import org.avmframework.objective.ObjectiveValue;
import org.avmframework.variable.AtomicVariable;
import org.avmframework.variable.Variable;
import org.avmframework.variable.VectorVariable;

public class AVM {

    protected LocalSearch localSearch;
    protected TerminationPolicy tp;
    protected Initializer initializer, restarter;

    public AVM(LocalSearch localSearch, TerminationPolicy tp, Initializer initializer) {
        this(localSearch, tp, initializer, initializer);
    }

    public AVM(LocalSearch localSearch, TerminationPolicy tp, Initializer initializer, Initializer restarter) {
        this.localSearch = localSearch;
        this.tp = tp;
        this.initializer = initializer;
        this.restarter = restarter;
    }

    public Monitor search(Vector vector, ObjectiveFunction objFun) {

        Monitor monitor = new Monitor(tp);
        objFun.setMonitor(monitor);

        try {
            // initialize the vector
            initializer.initialize(vector);

            do {
                boolean improvement;
                do {
                    improvement = false;
                    ObjectiveValue original = objFun.evaluate(vector);

                    // alternate through the variables
                    for (Variable var : vector.getVariables()) {

                        variableSearch(var, vector, objFun);

                        ObjectiveValue current = objFun.evaluate(vector);
                        if (current.betterThan(original)) {
                            improvement = true;
                        }
                    }
                } while (improvement);

                // restart the search
                monitor.observeRestart();
                restarter.initialize(vector);

            } while (true);

        } catch (TerminationException e) {
            // the search has ended
        }

        return monitor;
    }

    protected void variableSearch(Variable var, Vector vector, ObjectiveFunction objFun) throws TerminationException {
        if (var instanceof AtomicVariable) {
            atomicVariableSearch((AtomicVariable) var, vector, objFun);
        } else if (var instanceof VectorVariable) {
            vectorVariableSearch((VectorVariable) var, vector, objFun);
        }
    }

    protected void atomicVariableSearch(AtomicVariable av, Vector vector, ObjectiveFunction objFun) throws TerminationException {
        localSearch.search(av, vector, objFun);
    }

    protected void vectorVariableSearch(VectorVariable vv, Vector vector, ObjectiveFunction objFun) throws TerminationException {
    }
}