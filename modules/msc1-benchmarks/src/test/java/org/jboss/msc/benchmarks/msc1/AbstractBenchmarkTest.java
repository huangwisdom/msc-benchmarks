package org.jboss.msc.benchmarks.msc1;

import java.util.concurrent.TimeUnit;

import org.jboss.msc.service.ServiceContainer;
import org.junit.After;
import org.junit.Before;
//import org.jboss.msc.service.ServiceBuilderImpl; TODO: uncomment
//import org.jboss.msc.service.ServiceContainerImpl; TODO: uncomment

/**
 * Created by ropalka on 4/2/14.
 */
public class AbstractBenchmarkTest {

    // TODO: be careful when setting more than 8 threads here - MSC1 has special property for it
    static final int THREADS_COUNT = Integer.getInteger("jboss.msc.benchmark.threads.count", 8);
    static final int COMPLETE_GRAPH_SERVICES_COUNT = Integer.getInteger("jboss.msc.benchmark.complete.graph.services.count", 1001);
    static final int LINEAR_GRAPH_SERVICES_COUNT = Integer.getInteger("jboss.msc.benchmark.linear.graph.services.count", 1000/*1*/);
    static final int DISCRETE_GRAPH_SERVICES_COUNT = Integer.getInteger("jboss.msc.benchmark.discrete.graph.services.count", 100000);

    static ServiceContainer container;
    static ServiceInvocationStatistics statistics;

    @Before
    public void setUp() throws Exception {
        container = ServiceContainer.Factory.create(THREADS_COUNT, 30L, TimeUnit.SECONDS);
        statistics = new ServiceInvocationStatistics();
    }

    @After
    public void tearDown() throws Exception {
        final long startTime = System.nanoTime();
        container.shutdown();
        container.awaitTermination();
        final long nanoseconds = System.nanoTime() - startTime;
        System.out.println(" shutdown time: "+ (nanoseconds / 1000000));
        //ServiceContainerImpl.executionTime.set(0); TODO: uncomment
        //ServiceBuilderImpl.executionTime.set(0); TODO: uncomment
    }
}
