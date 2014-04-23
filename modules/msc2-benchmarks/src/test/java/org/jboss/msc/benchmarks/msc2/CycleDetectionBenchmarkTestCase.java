package org.jboss.msc.benchmarks.msc2;

import static org.junit.Assert.assertEquals;

import org.jboss.msc.service.ServiceMode;
import org.junit.Test;
//import org.jboss.msc.txn.CycleDetector; TODO: uncomment

/**
 * Created by ropalka on 4/2/14.
 */
public class CycleDetectionBenchmarkTestCase extends AbstractBenchmarkTest {



    @Test
    public void completeGraph() throws Exception {
        final long nanoseconds = CompleteGraph.benchmark(context, registry, ServiceMode.ON_DEMAND, txn, txnController, statistics, COMPLETE_GRAPH_SERVICES_COUNT, THREADS_COUNT);
        final int servicesCount = COMPLETE_GRAPH_SERVICES_COUNT;
        final String clazz = this.getClass().getName();
        final String method = ".completeGraph()";
//        final long denominator = 1000000 * THREADS_COUNT;
//        System.out.println(clazz + method + " cycle detection execution time: (services == " + servicesCount + ") " + (CycleDetector.executionTime.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [0] " + (ServiceBuilder.executionTime0.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [1] " + (ServiceBuilder.executionTime1.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [2] " + (ServiceBuilder.executionTime2.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [3] " + (ServiceBuilder.executionTime3.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [4] " + (ServiceBuilder.executionTime4.get() / denominator));
        System.out.println(clazz + method + " benchmark       execution time: (services == " + servicesCount + ") " + (nanoseconds / 1000000));
        assertEquals(0, statistics.getStartCallsCount());
        assertEquals(0, statistics.getStopCallsCount());
    }

    @Test
    public void linearGraph() throws Exception {
        final long nanoseconds = LinearGraph.benchmark(context, registry, ServiceMode.ON_DEMAND, txn, txnController, statistics, LINEAR_GRAPH_SERVICES_COUNT, THREADS_COUNT);
        final int servicesCount = LINEAR_GRAPH_SERVICES_COUNT;
        final String clazz = this.getClass().getName();
        final String method = ".linearGraph()";
//        final long denominator = 1000000 * THREADS_COUNT;
//        System.out.println(clazz + method + " cycle detection execution time: (services == " + servicesCount + ") " + (CycleDetector.executionTime.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [0] " + (ServiceBuilder.executionTime0.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [1] " + (ServiceBuilder.executionTime1.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [2] " + (ServiceBuilder.executionTime2.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [3] " + (ServiceBuilder.executionTime3.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [4] " + (ServiceBuilder.executionTime4.get() / denominator));
        System.out.println(clazz + method + " benchmark       execution time: (services == " + servicesCount + ") " + (nanoseconds / 1000000));
        assertEquals(0, statistics.getStartCallsCount());
        assertEquals(0, statistics.getStopCallsCount());
    }

    @Test
    public void discreteGraph() throws Exception {
        final long nanoseconds = DiscreteGraph.benchmark(context, registry, ServiceMode.ON_DEMAND, txn, txnController, statistics, DISCRETE_GRAPH_SERVICES_COUNT, THREADS_COUNT);
        final int servicesCount = DISCRETE_GRAPH_SERVICES_COUNT;
        final String clazz = this.getClass().getName();
        final String method = ".discreteGraph()";
//        final long denominator = 1000000 * THREADS_COUNT;
//        System.out.println(clazz + method + " cycle detection execution time: (services == " + servicesCount + ") " + (CycleDetector.executionTime.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [0] " + (ServiceBuilder.executionTime0.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [1] " + (ServiceBuilder.executionTime1.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [2] " + (ServiceBuilder.executionTime2.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [3] " + (ServiceBuilder.executionTime3.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [4] " + (ServiceBuilder.executionTime4.get() / denominator));
        System.out.println(clazz + method + " benchmark       execution time: (services == " + servicesCount + ") " + (nanoseconds / 1000000));
        assertEquals(0, statistics.getStartCallsCount());
        assertEquals(0, statistics.getStopCallsCount());
    }

}
