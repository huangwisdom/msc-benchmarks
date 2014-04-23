package org.jboss.msc.benchmarks.msc2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jboss.msc.service.ServiceMode;
import org.junit.Test;

/**
 * Benchmark tests for complete service installation (including service start).
 * 
 * @author <a href="mailto:frainone@redhat.com">Flavia Rainone</a>
 *
 */
public class ServiceStartBenchmarkTestCase extends AbstractBenchmarkTest {

    @Test
    public void completeGraph() throws Exception {
        final long nanoseconds = CompleteGraph.benchmark(context, registry, ServiceMode.ACTIVE, txn, txnController, statistics, COMPLETE_GRAPH_SERVICES_COUNT, THREADS_COUNT);
        final int servicesCount = COMPLETE_GRAPH_SERVICES_COUNT;
        final String clazz = this.getClass().getName();
        final String method = ".completeGraph()";
        System.out.println(clazz + method + " benchmark       execution time: (services == " + servicesCount + ") " + (nanoseconds / 1000000));
        assertEquals(servicesCount, statistics.getStartCallsCount());
        assertEquals(0, statistics.getStopCallsCount());
    }

    @Test
    public void linearGraph() throws Exception {
        final long nanoseconds = LinearGraph.benchmark(context, registry, ServiceMode.ACTIVE, txn, txnController, statistics, LINEAR_GRAPH_SERVICES_COUNT, THREADS_COUNT);
        final int servicesCount = LINEAR_GRAPH_SERVICES_COUNT;
        final String clazz = this.getClass().getName();
        final String method = ".linearGraph()";
        System.out.println(clazz + method + " benchmark       execution time: (services == " + servicesCount + ") " + (nanoseconds / 1000000));
        assertEquals(servicesCount, statistics.getStartCallsCount());
        assertTrue(statistics.getStopCallsCount() == 0);
    }

    @Test
    public void discreteGraph() throws Exception {
        final long nanoseconds = DiscreteGraph.benchmark(context, registry, ServiceMode.ACTIVE, txn, txnController, statistics, DISCRETE_GRAPH_SERVICES_COUNT, THREADS_COUNT);
        final int servicesCount = DISCRETE_GRAPH_SERVICES_COUNT;
        final String clazz = this.getClass().getName();
        final String method = ".discreteGraph()";
        System.out.println(clazz + method + " benchmark       execution time: (services == " + servicesCount + ") " + (nanoseconds / 1000000));
        assertEquals(servicesCount, statistics.getStartCallsCount());
        assertTrue(statistics.getStopCallsCount() == 0);
    }

}
