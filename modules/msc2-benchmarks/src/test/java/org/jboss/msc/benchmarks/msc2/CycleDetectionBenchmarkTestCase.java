package org.jboss.msc.benchmarks.msc2;

import static org.jboss.msc.service.DependencyFlag.UNREQUIRED;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceContainer;
import org.jboss.msc.service.ServiceContext;
import org.jboss.msc.service.ServiceMode;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistry;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.txn.BasicTransaction;
import org.jboss.msc.txn.CommitResult;
import org.jboss.msc.txn.CompletionListener;
//import org.jboss.msc.txn.CycleDetector; TODO: uncomment
import org.jboss.msc.txn.PrepareResult;
import org.jboss.msc.txn.TransactionController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertTrue;

/**
 * Created by ropalka on 4/2/14.
 */
public class CycleDetectionBenchmarkTestCase {

    private static final int THREADS_COUNT = Integer.getInteger("jboss.msc.benchmark.threads.count", 8);
    private static final int COMPLETE_GRAPH_SERVICES_COUNT = Integer.getInteger("jboss.msc.benchmark.complete.graph.services.count", 1001);
    private static final int LINEAR_GRAPH_SERVICES_COUNT = Integer.getInteger("jboss.msc.benchmark.linear.graph.services.count", 10001);
    private static final int DISCRETE_GRAPH_SERVICES_COUNT = Integer.getInteger("jboss.msc.benchmark.discrete.graph.services.count", 100000);

    private static TransactionController txnController;
    private static ServiceContainer container;
    private static ServiceRegistry registry;
    private static ServiceContext context;
    private static ThreadPoolExecutor executor;
    private static BasicTransaction txn;
    private static ServiceInvocationStatistics statistics;
    private static CountingService service;

    @Before
    public void setUp() throws Exception {
        txnController = TransactionController.createInstance();
        container = txnController.createServiceContainer();
        registry = container.newRegistry();
        context = txnController.getServiceContext();
        executor = new ThreadPoolExecutor(THREADS_COUNT, THREADS_COUNT, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        txn = txnController.createTransaction(executor);
        service = new CountingService(statistics = new ServiceInvocationStatistics());
    }

    @After
    public void tearDown() throws Exception {
        final BasicTransaction shutDownTxn = txnController.createTransaction(executor);
        container.shutdown(shutDownTxn);
        prepareAndCommit(txnController, shutDownTxn);
        executor.shutdown();
//        CycleDetector.executionTime.set(0); TODO: uncomment
//        ServiceBuilder.executionTime0.set(0);
//        ServiceBuilder.executionTime1.set(0);
//        ServiceBuilder.executionTime2.set(0);
//        ServiceBuilder.executionTime3.set(0);
//        ServiceBuilder.executionTime4.set(0);
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
        prepareAndCommit(txnController, txn);
        final long endTime = System.nanoTime();
        final long denominator = 1000000 * THREADS_COUNT;
        final int servicesCount = COMPLETE_GRAPH_SERVICES_COUNT;
        final String clazz = this.getClass().getName();
        final String method = ".cycleDetectionCompleteGraphAllServicesDown()";
//        System.out.println(clazz + method + " cycle detection execution time: (services == " + servicesCount + ") " + (CycleDetector.executionTime.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [0] " + (ServiceBuilder.executionTime0.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [1] " + (ServiceBuilder.executionTime1.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [2] " + (ServiceBuilder.executionTime2.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [3] " + (ServiceBuilder.executionTime3.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [4] " + (ServiceBuilder.executionTime4.get() / denominator));
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
        prepareAndCommit(txnController, txn);
        final long endTime = System.nanoTime();
        final long denominator = 1000000 * THREADS_COUNT;
        final int servicesCount = LINEAR_GRAPH_SERVICES_COUNT;
        final String clazz = this.getClass().getName();
        final String method = ".cycleDetectionLinearGraphAllServicesDown()";
//        System.out.println(clazz + method + " cycle detection execution time: (services == " + servicesCount + ") " + (CycleDetector.executionTime.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [0] " + (ServiceBuilder.executionTime0.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [1] " + (ServiceBuilder.executionTime1.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [2] " + (ServiceBuilder.executionTime2.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [3] " + (ServiceBuilder.executionTime3.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [4] " + (ServiceBuilder.executionTime4.get() / denominator));
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
        prepareAndCommit(txnController, txn);
        final long endTime = System.nanoTime();
        final long denominator = 1000000 * THREADS_COUNT;
        final int servicesCount = DISCRETE_GRAPH_SERVICES_COUNT;
        final String clazz = this.getClass().getName();
        final String method = ".cycleDetectionDiscreteGraphAllServicesDown()";
//        System.out.println(clazz + method + " cycle detection execution time: (services == " + servicesCount + ") " + (CycleDetector.executionTime.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [0] " + (ServiceBuilder.executionTime0.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [1] " + (ServiceBuilder.executionTime1.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [2] " + (ServiceBuilder.executionTime2.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [3] " + (ServiceBuilder.executionTime3.get() / denominator));
//        System.out.println(clazz + method + " builder install execution time: (services == " + servicesCount + ") [4] " + (ServiceBuilder.executionTime4.get() / denominator));
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
                    builder = context.addService(CountingService.class, registry, ServiceName.of("" + i), txn).setService(service);
                    builder.setMode(ServiceMode.ON_DEMAND);
                    for (int j = COMPLETE_GRAPH_SERVICES_COUNT - 1; j > i; j--) builder.addDependency(ServiceName.of("" + j), UNREQUIRED);
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
                    builder = context.addService(CountingService.class, registry, ServiceName.of("" + i), txn).setService(service);
                    builder.setMode(ServiceMode.ON_DEMAND);
                    builder.addDependency(ServiceName.of("" + (i + 1)), UNREQUIRED);
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
                    builder = context.addService(CountingService.class, registry, ServiceName.of("" + i), txn).setService(service);
                    builder.setMode(ServiceMode.ON_DEMAND);
                    builder.install();
                }
            } finally {
                threadsFinishedSignal.countDown();
            }
        }
    }

    private static void prepareAndCommit(final TransactionController txnController, final BasicTransaction txn) {
        final CompletionListener<PrepareResult<BasicTransaction>> prepareListener = new CompletionListener<>();
        txnController.prepare(txn, prepareListener);
        prepareListener.awaitCompletionUninterruptibly();
        final CompletionListener<CommitResult<BasicTransaction>> commitListener = new CompletionListener<>();
        txnController.commit(txn, commitListener);
        commitListener.awaitCompletionUninterruptibly();
    }

    private static final class CountingService implements Service {

        private final ServiceInvocationStatistics statistics;

        CountingService(final ServiceInvocationStatistics statistics) {
            this.statistics = statistics;
        }

        @Override
        public void start(final StartContext context) {
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
