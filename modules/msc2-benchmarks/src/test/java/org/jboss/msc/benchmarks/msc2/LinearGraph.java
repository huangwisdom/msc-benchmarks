/*
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.msc.benchmarks.msc2;

import static org.jboss.msc.benchmarks.msc2.AbstractBenchmarkTest.prepareAndCommit;
import static org.jboss.msc.service.DependencyFlag.UNREQUIRED;

import java.util.concurrent.CountDownLatch;

import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceContext;
import org.jboss.msc.service.ServiceMode;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistry;
import org.jboss.msc.txn.BasicTransaction;
import org.jboss.msc.txn.TransactionController;

/**
 * @author <a href="mailto:ropalka@redhat.com">Richard Opalka</a>
 * @author <a href="mailto:frainone@redhat.com">Flavia Rainone</a>
 *
 */
final class LinearGraph {

    static final long benchmark (final ServiceContext context, final ServiceRegistry registry, final ServiceMode mode,
            final BasicTransaction txn, final TransactionController txnController,  final ServiceInvocationStatistics statistics, final int servicesCount, final int threadsCount) throws InterruptedException {
        final int range = servicesCount / threadsCount;
        final CountDownLatch threadsInitializedSignal = new CountDownLatch(threadsCount);
        final CountDownLatch runBenchmarkSignal = new CountDownLatch(1);
        final CountDownLatch threadsFinishedSignal = new CountDownLatch(threadsCount);
        int leftClosedIntervalIndex = 0, rightOpenIntervalIndex = range + (servicesCount % threadsCount);
        new Thread(new InstallTask(threadsInitializedSignal, runBenchmarkSignal, threadsFinishedSignal,
                leftClosedIntervalIndex, rightOpenIntervalIndex, context, registry, mode, txn, statistics)).start();
        for (int i = 1; i < threadsCount; i++) {
            leftClosedIntervalIndex = rightOpenIntervalIndex;
            rightOpenIntervalIndex += range;
            new Thread(new InstallTask(threadsInitializedSignal, runBenchmarkSignal, threadsFinishedSignal,
                    leftClosedIntervalIndex, rightOpenIntervalIndex, context, registry, mode, txn, statistics)).start();
        }
        threadsInitializedSignal.await();
        final long startTime = System.nanoTime();
        runBenchmarkSignal.countDown();
        threadsFinishedSignal.await();
        prepareAndCommit(txnController, txn);
        return System.nanoTime() - startTime;
    }

    private static final class InstallTask implements Runnable {

        private final CountDownLatch threadsInitializedSignal;
        private final CountDownLatch runBenchmarkSignal;
        private final CountDownLatch threadsFinishedSignal;
        private final int leftClosedIntervalIndex;
        private final int rightOpenIntervalIndex;
        private final ServiceContext context;
        private final ServiceRegistry registry;
        private final ServiceMode mode;
        private final BasicTransaction txn;
        private final ServiceInvocationStatistics statistics;

        private InstallTask(final CountDownLatch threadsInitializedSignal, final CountDownLatch runBenchmarkSignal, final CountDownLatch threadsFinishedSignal,
                                         final int leftClosedIntervalIndex, final int rightOpenIntervalIndex, final ServiceContext context,
                                         final ServiceRegistry registry, final ServiceMode mode, final BasicTransaction txn, final ServiceInvocationStatistics statistics) {
            this.threadsInitializedSignal = threadsInitializedSignal;
            this.runBenchmarkSignal = runBenchmarkSignal;
            this.threadsFinishedSignal = threadsFinishedSignal;
            this.leftClosedIntervalIndex = leftClosedIntervalIndex;
            this.rightOpenIntervalIndex = rightOpenIntervalIndex;
            this.context = context;
            this.registry = registry;
            this.mode = mode;
            this.txn = txn;
            this.statistics = statistics;
        }

        public void run() {
            threadsInitializedSignal.countDown();
            while (true) try {runBenchmarkSignal.await(); break; } catch (Exception ignore) {}
            try {
                ServiceBuilder builder;
                for (int i = leftClosedIntervalIndex; i < rightOpenIntervalIndex; i++) {
                    builder = context.addService(CountingService.class, registry, ServiceName.of("" + i), txn).setService(new CountingService(statistics));
                    builder.setMode(mode);
                    if (i + 1 != rightOpenIntervalIndex) {
                        builder.addDependency(ServiceName.of("" + (i + 1)), UNREQUIRED);
                    }
                    builder.install();
                }
            } finally {
                threadsFinishedSignal.countDown();
            }
        }
    }

}