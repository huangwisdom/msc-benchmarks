package org.jboss.msc.benchmarks.msc1;

import static org.junit.Assert.assertTrue;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceBuilder;
//import org.jboss.msc.service.ServiceBuilderImpl; TODO: uncomment
import org.jboss.msc.service.ServiceContainer;
//import org.jboss.msc.service.ServiceContainerImpl; TODO: uncomment
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ropalka on 4/2/14.
 */
public class CycleDetectionBenchmarkTestCase {

    // TODO: be careful when setting more than 8 threads here - MSC1 has special property for it
    private static final int THREADS_COUNT = Integer.getInteger("jboss.msc.benchmark.threads.count", 8);
    private static final int COMPLETE_GRAPH_SERVICES_COUNT = Integer.getInteger("jboss.msc.benchmark.complete.graph.services.count", 1001);
    private static final int LINEAR_GRAPH_SERVICES_COUNT = Integer.getInteger("jboss.msc.benchmark.linear.graph.services.count", 10001);
    private static final int DISCRETE_GRAPH_SERVICES_COUNT = Integer.getInteger("jboss.msc.benchmark.discrete.graph.services.count", 100000);

    private static ServiceContainer container;
    private static ServiceInvocationStatistics statistics;

    @Before
    public void setUp() throws Exception {
        container = ServiceContainer.Factory.create(THREADS_COUNT, 30L, TimeUnit.SECONDS);
        statistics = new ServiceInvocationStatistics();
    }

    @After
    public void tearDown() throws Exception {
        container.shutdown();
        container.awaitTermination();
        //ServiceContainerImpl.executionTime.set(0); TODO: uncomment
        //ServiceBuilderImpl.executionTime.set(0); TODO: uncomment
    }

    @Test
    public void cycleDetectionCompleteGraphAllServicesDown() throws Exception {
        final int range = (COMPLETE_GRAPH_SERVICES_COUNT - 1) / THREADS_COUNT;
        final CountDownLatch threadsInitializedSignal = new CountDownLatch(THREADS_COUNT);
        final CountDownLatch runBenchmarkSignal = new CountDownLatch(1);
        final CountDownLatch threadsFinishedSignal = new CountDownLatch(THREADS_COUNT);
        int leftClosedIntervalIndex, rightOpenIntervalIndex;
        for (int i = 0; i < THREADS_COUNT; i++) {
            leftClosedIntervalIndex = range * i;
            rightOpenIntervalIndex = range * (i + 1);
            new Thread(new CompleteGraphInstallTask(threadsInitializedSignal, runBenchmarkSignal, threadsFinishedSignal, leftClosedIntervalIndex, rightOpenIntervalIndex)).start();
        }
        threadsInitializedSignal.await();
        final long startTime = System.nanoTime();
        runBenchmarkSignal.countDown();
        threadsFinishedSignal.await();
        container.awaitStability();
        final long endTime = System.nanoTime();
        final long denominator = 1000000 * THREADS_COUNT;
        final int servicesCount = COMPLETE_GRAPH_SERVICES_COUNT;
        final String clazz = this.getClass().getName();
        final String method = ".cycleDetectionCompleteGraphAllServicesDown()";
        //System.out.println(clazz + method + " cycle detection execution time: (services == " + servicesCount + ") " + (ServiceContainerImpl.executionTime.get() / denominator));
        //System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") " + (ServiceBuilderImpl.executionTime.get() / denominator));
        System.out.println(clazz + method + " benchmark       execution time: (services == " + servicesCount + ") " + ((endTime - startTime) / 1000000));
        assertTrue(statistics.getStartCallsCount() == 0);
        assertTrue(statistics.getStopCallsCount() == 0);
    }

    @Test
    public void cycleDetectionLinearGraphAllServicesDown() throws Exception {
        final int range = (LINEAR_GRAPH_SERVICES_COUNT - 1) / THREADS_COUNT;
        final CountDownLatch threadsInitializedSignal = new CountDownLatch(THREADS_COUNT);
        final CountDownLatch runBenchmarkSignal = new CountDownLatch(1);
        final CountDownLatch threadsFinishedSignal = new CountDownLatch(THREADS_COUNT);
        int leftClosedIntervalIndex, rightOpenIntervalIndex;
        for (int i = 0; i < THREADS_COUNT; i++) {
            leftClosedIntervalIndex = range * i;
            rightOpenIntervalIndex = range * (i + 1);
            new Thread(new LinearGraphInstallTask(threadsInitializedSignal, runBenchmarkSignal, threadsFinishedSignal, leftClosedIntervalIndex, rightOpenIntervalIndex)).start();
        }
        threadsInitializedSignal.await();
        final long startTime = System.nanoTime();
        runBenchmarkSignal.countDown();
        threadsFinishedSignal.await();
        container.awaitStability();
        final long endTime = System.nanoTime();
        final long denominator = 1000000 * THREADS_COUNT;
        final int servicesCount = LINEAR_GRAPH_SERVICES_COUNT;
        final String clazz = this.getClass().getName();
        final String method = ".cycleDetectionLinearGraphAllServicesDown()";
        //System.out.println(clazz + method + " cycle detection execution time: (services == " + servicesCount + ") " + (ServiceContainerImpl.executionTime.get() / denominator));
        //System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") " + (ServiceBuilderImpl.executionTime.get() / denominator));
        System.out.println(clazz + method + " benchmark       execution time: (services == " + servicesCount + ") " + ((endTime - startTime) / 1000000));
        assertTrue(statistics.getStartCallsCount() == 0);
        assertTrue(statistics.getStopCallsCount() == 0);
    }

    @Test
    public void cycleDetectionDiscreteGraphAllServicesDown() throws Exception {
        final int range = DISCRETE_GRAPH_SERVICES_COUNT / THREADS_COUNT;
        final CountDownLatch threadsInitializedSignal = new CountDownLatch(THREADS_COUNT);
        final CountDownLatch runBenchmarkSignal = new CountDownLatch(1);
        final CountDownLatch threadsFinishedSignal = new CountDownLatch(THREADS_COUNT);
        int leftClosedIntervalIndex, rightOpenIntervalIndex;
        for (int i = 0; i < THREADS_COUNT; i++) {
            leftClosedIntervalIndex = range * i;
            rightOpenIntervalIndex = range * (i + 1);
            new Thread(new DiscreteGraphInstallTask(threadsInitializedSignal, runBenchmarkSignal, threadsFinishedSignal, leftClosedIntervalIndex, rightOpenIntervalIndex)).start();
        }
        threadsInitializedSignal.await();
        final long startTime = System.nanoTime();
        runBenchmarkSignal.countDown();
        threadsFinishedSignal.await();
        container.awaitStability();
        final long endTime = System.nanoTime();
        final long denominator = 1000000 * THREADS_COUNT;
        final int servicesCount = DISCRETE_GRAPH_SERVICES_COUNT;
        final String clazz = this.getClass().getName();
        final String method = ".cycleDetectionDiscreteGraphAllServicesDown()";
        //System.out.println(clazz + method + " cycle detection execution time: (services == " + servicesCount + ") " + (ServiceContainerImpl.executionTime.get() / denominator));
        //System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") " + (ServiceBuilderImpl.executionTime.get() / denominator));
        System.out.println(clazz + method + " benchmark       execution time: (services == " + servicesCount + ") " + ((endTime - startTime) / 1000000));
        assertTrue(statistics.getStartCallsCount() == 0);
        assertTrue(statistics.getStopCallsCount() == 0);
    }

    private static final class CompleteGraphInstallTask implements Runnable {

        private final CountDownLatch threadsInitializedSignal;
        private final CountDownLatch runBenchmarkSignal;
        private final CountDownLatch threadsFinishedSignal;
        private final int leftClosedIntervalIndex;
        private final int rightOpenIntervalIndex;

        private CompleteGraphInstallTask(final CountDownLatch threadsInitializedSignal, final CountDownLatch runBenchmarkSignal, final CountDownLatch threadsFinishedSignal,
                                         final int leftClosedIntervalIndex, final int rightOpenIntervalIndex) {
            this.threadsInitializedSignal = threadsInitializedSignal;
            this.runBenchmarkSignal = runBenchmarkSignal;
            this.threadsFinishedSignal = threadsFinishedSignal;
            this.leftClosedIntervalIndex = leftClosedIntervalIndex;
            this.rightOpenIntervalIndex = rightOpenIntervalIndex;
        }

        public void run() {
            threadsInitializedSignal.countDown();
            while (true) try {runBenchmarkSignal.await(); break; } catch (Exception ignore) {}
            try {
                ServiceBuilder builder;
                for (int i = leftClosedIntervalIndex; i < rightOpenIntervalIndex; i++) {
                    builder = container.addService(ServiceName.of("" + i), new CountingService(statistics));
                    builder.setInitialMode(ServiceController.Mode.ON_DEMAND);
                    for (int j = COMPLETE_GRAPH_SERVICES_COUNT - 1; j > i; j--) builder.addDependency(ServiceName.of("" + j));
                    builder.install();
                }
            } finally {
                threadsFinishedSignal.countDown();
            }
        }
    }

    private static final class LinearGraphInstallTask implements Runnable {

        private final CountDownLatch threadsInitializedSignal;
        private final CountDownLatch runBenchmarkSignal;
        private final CountDownLatch threadsFinishedSignal;
        private final int leftClosedIntervalIndex;
        private final int rightOpenIntervalIndex;

        private LinearGraphInstallTask(final CountDownLatch threadsInitializedSignal, final CountDownLatch runBenchmarkSignal, final CountDownLatch threadsFinishedSignal,
                                         final int leftClosedIntervalIndex, final int rightOpenIntervalIndex) {
            this.threadsInitializedSignal = threadsInitializedSignal;
            this.runBenchmarkSignal = runBenchmarkSignal;
            this.threadsFinishedSignal = threadsFinishedSignal;
            this.leftClosedIntervalIndex = leftClosedIntervalIndex;
            this.rightOpenIntervalIndex = rightOpenIntervalIndex;
        }

        public void run() {
            threadsInitializedSignal.countDown();
            while (true) try {runBenchmarkSignal.await(); break; } catch (Exception ignore) {}
            try {
                ServiceBuilder builder;
                for (int i = leftClosedIntervalIndex; i < rightOpenIntervalIndex; i++) {
                    builder = container.addService(ServiceName.of("" + i), new CountingService(statistics));
                    builder.setInitialMode(ServiceController.Mode.ON_DEMAND);
                    builder.addDependency(ServiceName.of("" + (i + 1)));
                    builder.install();
                }
            } finally {
                threadsFinishedSignal.countDown();
            }
        }
    }

    private static final class DiscreteGraphInstallTask implements Runnable {

        private final CountDownLatch threadsInitializedSignal;
        private final CountDownLatch runBenchmarkSignal;
        private final CountDownLatch threadsFinishedSignal;
        private final int leftClosedIntervalIndex;
        private final int rightOpenIntervalIndex;

        private DiscreteGraphInstallTask(final CountDownLatch threadsInitializedSignal, final CountDownLatch runBenchmarkSignal, final CountDownLatch threadsFinishedSignal,
                                       final int leftClosedIntervalIndex, final int rightOpenIntervalIndex) {
            this.threadsInitializedSignal = threadsInitializedSignal;
            this.runBenchmarkSignal = runBenchmarkSignal;
            this.threadsFinishedSignal = threadsFinishedSignal;
            this.leftClosedIntervalIndex = leftClosedIntervalIndex;
            this.rightOpenIntervalIndex = rightOpenIntervalIndex;
        }

        public void run() {
            threadsInitializedSignal.countDown();
            while (true) try {runBenchmarkSignal.await(); break; } catch (Exception ignore) {}
            try {
                ServiceBuilder builder;
                for (int i = leftClosedIntervalIndex; i < rightOpenIntervalIndex; i++) {
                    builder = container.addService(ServiceName.of("" + i), new CountingService(statistics));
                    builder.setInitialMode(ServiceController.Mode.ON_DEMAND);
                    builder.install();
                }
            } finally {
                threadsFinishedSignal.countDown();
            }
        }
    }

    private static final class CountingService implements Service<Void> {

        private final ServiceInvocationStatistics statistics;

        CountingService(final ServiceInvocationStatistics statistics) {
            this.statistics = statistics;
        }

        @Override
        public void start(final StartContext context) throws StartException {
            try {
                statistics.startCallsCount.incrementAndGet();
            } finally {
                context.complete();
            }
        }

        @Override
        public void stop(final StopContext context) {
            try {
                statistics.stopCallsCount.incrementAndGet();
            } finally {
                context.complete();
            }
        }

        @Override
        public Void getValue() throws IllegalStateException, IllegalArgumentException {
            return null;
        }

    }

    private static final class ServiceInvocationStatistics {

        private final AtomicInteger startCallsCount = new AtomicInteger();
        private final AtomicInteger stopCallsCount = new AtomicInteger();

        int getStartCallsCount() {
            return startCallsCount.get();
        }

        int getStopCallsCount() {
            return stopCallsCount.get();
        }

    }

}
