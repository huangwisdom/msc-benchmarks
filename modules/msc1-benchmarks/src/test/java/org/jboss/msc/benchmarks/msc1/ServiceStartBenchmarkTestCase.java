package org.jboss.msc.benchmarks.msc1;

import static org.junit.Assert.assertEquals;

import org.jboss.msc.service.ServiceController;
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
        final long nanoseconds = CompleteGraph.benchmark(container, ServiceController.Mode.ACTIVE, statistics, COMPLETE_GRAPH_SERVICES_COUNT, THREADS_COUNT);
        final int servicesCount = COMPLETE_GRAPH_SERVICES_COUNT;
        final String clazz = this.getClass().getName();
        final String method = ".completeGraph()";
        System.out.println(clazz + method + " benchmark       execution time: (services == " + servicesCount + ") " + (nanoseconds / 1000000));
        assertEquals(servicesCount, statistics.getStartCallsCount());
        assertEquals(0, statistics.getStopCallsCount());
    }

    @Test
    public void linearGraph() throws Exception {
        final long nanoseconds = LinearGraph.benchmark(container, ServiceController.Mode.ACTIVE, statistics, LINEAR_GRAPH_SERVICES_COUNT, THREADS_COUNT);
        final int servicesCount = LINEAR_GRAPH_SERVICES_COUNT;
        final String clazz = this.getClass().getName();
        final String method = ".linearGraph()";
        System.out.println(clazz + method + " benchmark       execution time: (services == " + servicesCount + ") " + (nanoseconds / 1000000));
        assertEquals(servicesCount, statistics.getStartCallsCount());
        assertEquals(0, statistics.getStopCallsCount());
    }

    @Test
    public void discreteGraph() throws Exception {
        final long nanoseconds = DiscreteGraph.benchmark(container, ServiceController.Mode.ACTIVE,  statistics,  DISCRETE_GRAPH_SERVICES_COUNT, THREADS_COUNT);
        final int servicesCount = DISCRETE_GRAPH_SERVICES_COUNT;
        final String clazz = this.getClass().getName();
        final String method = ".discreteGraph()";
        System.out.println(clazz + method + " benchmark       execution time: (services == " + servicesCount + ") " + (nanoseconds / 1000000));
        assertEquals(servicesCount, statistics.getStartCallsCount());
        assertEquals(0, statistics.getStopCallsCount());
    }

}
