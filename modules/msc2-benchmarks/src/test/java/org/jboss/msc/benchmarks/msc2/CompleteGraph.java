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
import org.jboss.msc.txn.UpdateTransaction;
import org.jboss.msc.txn.TransactionController;

/**
 * @author <a href="mailto:ropalka@redhat.com">Richard Opalka</a>
 * @author <a href="mailto:frainone@redhat.com">Flavia Rainone</a>
 */
final class CompleteGraph {

    private static volatile Throwable failure;

    static long benchmark(final ServiceContext context, final ServiceRegistry registry, final ServiceMode mode,
            final UpdateTransaction txn, final TransactionController txnController, final CountingService service, final int servicesCount, final int threadsCount) throws InterruptedException {
        try {
            final int range = servicesCount / threadsCount;
            final CountDownLatch threadsInitializedSignal = new CountDownLatch(threadsCount);
            final CountDownLatch runBenchmarkSignal = new CountDownLatch(1);
            final CountDownLatch threadsFinishedSignal = new CountDownLatch(threadsCount);
            int leftClosedIntervalIndex = 0, rightOpenIntervalIndex = range + (servicesCount % threadsCount);
            new Thread(new InstallTask(threadsInitializedSignal, runBenchmarkSignal, threadsFinishedSignal,
                    leftClosedIntervalIndex, rightOpenIntervalIndex, servicesCount, context, registry, mode, txn, service)).start();
            for (int i = 1; i < threadsCount; i++) {
                leftClosedIntervalIndex = rightOpenIntervalIndex;
                rightOpenIntervalIndex += range;
                new Thread(new InstallTask(threadsInitializedSignal, runBenchmarkSignal, threadsFinishedSignal,
                        leftClosedIntervalIndex, rightOpenIntervalIndex, servicesCount, context, registry, mode, txn, service)).start();
            }
            threadsInitializedSignal.await();
            final long startTime = System.nanoTime();
            runBenchmarkSignal.countDown();
            threadsFinishedSignal.await();
            prepareAndCommit(txnController, txn);
            return failure == null ? System.nanoTime() - startTime : 0;
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            return 0;
        }
    }
    
    
    private static final class InstallTask implements Runnable {

        private final CountDownLatch threadsInitializedSignal;
        private final CountDownLatch runBenchmarkSignal;
        private final CountDownLatch threadsFinishedSignal;
        private final int leftClosedIntervalIndex;
        private final int rightOpenIntervalIndex;
        private final int servicesCount;
        private final ServiceContext context;
        private final ServiceRegistry registry;
        private final ServiceMode mode;
        private final UpdateTransaction txn;
        private final CountingService service;

        private InstallTask(final CountDownLatch threadsInitializedSignal, final CountDownLatch runBenchmarkSignal, final CountDownLatch threadsFinishedSignal,
                                   final int leftClosedIntervalIndex, final int rightOpenIntervalIndex, final int servicesCount, final ServiceContext context,
                                   final ServiceRegistry registry, final ServiceMode mode, final UpdateTransaction txn, final CountingService service) {
            this.threadsInitializedSignal = threadsInitializedSignal;
            this.runBenchmarkSignal = runBenchmarkSignal;
            this.threadsFinishedSignal = threadsFinishedSignal;
            this.leftClosedIntervalIndex = leftClosedIntervalIndex;
            this.rightOpenIntervalIndex = rightOpenIntervalIndex;
            this.servicesCount = servicesCount;
            this.context = context;
            this.registry = registry;
            this.mode = mode;
            this.txn = txn;
            this.service = service;
        }

        public void run() {
            threadsInitializedSignal.countDown();
            while (true) try {runBenchmarkSignal.await(); break; } catch (Exception ignore) {}
            try {
                ServiceBuilder builder;
                for (int i = leftClosedIntervalIndex; i < rightOpenIntervalIndex; i++) {
                    builder = context.addService(registry, ServiceName.of("" + i)).setService(service);
                    builder.setMode(mode);
                    for (int j = servicesCount - 1; j > i; j--)
                        builder.addDependency(ServiceName.of("" + j), UNREQUIRED);
                    builder.install();
                }
            } catch (Throwable t) {
                failure = t;
                throw t;
            } finally {
                threadsFinishedSignal.countDown();
            }
        }
    }

}
