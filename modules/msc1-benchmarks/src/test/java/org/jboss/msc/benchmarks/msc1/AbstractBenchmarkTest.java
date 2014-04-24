package org.jboss.msc.benchmarks.msc1;

import static org.jboss.msc.benchmarks.framework.BenchmarksConfig.*;

import java.util.concurrent.TimeUnit;

import org.jboss.msc.benchmarks.framework.ServiceInvocationStatistics;
import org.jboss.msc.service.ServiceContainer;
import org.junit.After;
import org.junit.Before;
//import org.jboss.msc.service.ServiceBuilderImpl; TODO: uncomment
//import org.jboss.msc.service.ServiceContainerImpl; TODO: uncomment

/**
 * Created by ropalka on 4/2/14.
 */
public class AbstractBenchmarkTest {

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
