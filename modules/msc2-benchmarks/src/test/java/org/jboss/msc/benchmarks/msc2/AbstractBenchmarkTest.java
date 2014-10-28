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

import static org.jboss.msc.benchmarks.framework.BenchmarksConfig.*;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jboss.msc.benchmarks.framework.ServiceInvocationStatistics;
import org.jboss.msc.service.ServiceContainer;
import org.jboss.msc.service.ServiceContext;
import org.jboss.msc.service.ServiceRegistry;
import org.jboss.msc.txn.UpdateTransaction;
import org.jboss.msc.txn.CommitResult;
import org.jboss.msc.txn.PrepareResult;
import org.jboss.msc.txn.TransactionController;
import org.jboss.msc.util.CompletionListener;
import org.junit.After;
import org.junit.Before;

/**
 * @author <a href="mailto:ropalka@redhat.com">Richard Opalka</a>
 * @author <a href="mailto:frainone@redhat.com">Flavia Rainone</a>
 *
 */
public class AbstractBenchmarkTest {

    private static ServiceContainer container;
    static TransactionController txnController;
    static ServiceRegistry registry;
    static ServiceContext context;
    static ThreadPoolExecutor executor;
    static UpdateTransaction txn;
    static ServiceInvocationStatistics statistics;
    static CountingService service;

    @Before
    public void setUp() throws Exception {
        txnController = TransactionController.createInstance();
        container = txnController.createServiceContainer();
        registry = container.newRegistry();
        context = txnController.getServiceContext();
        executor = new ThreadPoolExecutor(MSC_THREADS_COUNT, MSC_THREADS_COUNT, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        final CompletionListener<UpdateTransaction> listener = new CompletionListener<>();
        txnController.createUpdateTransaction(executor, listener);
        txn = listener.awaitCompletionUninterruptibly();
        statistics = new ServiceInvocationStatistics();
        service = new CountingService(statistics);
    }

    @After
    public void tearDown() throws Exception {
        final long startTime = System.nanoTime();
        final CompletionListener<UpdateTransaction> listener = new CompletionListener<>();
        txnController.createUpdateTransaction(executor, listener);
        final UpdateTransaction shutDownTxn = listener.awaitCompletionUninterruptibly();
        container.shutdown(shutDownTxn);
        prepareAndCommit(txnController, shutDownTxn);
        final long nanoseconds = System.nanoTime() - startTime;
        System.out.println("Shutdown time: "+ (nanoseconds / 1000000));
        executor.shutdown();
    }

    public static void prepareAndCommit(final TransactionController txnController, final UpdateTransaction txn) {
        final CompletionListener<PrepareResult<UpdateTransaction>> prepareListener = new CompletionListener<>();
        txnController.prepare(txn, prepareListener);
        prepareListener.awaitCompletionUninterruptibly();
        final CompletionListener<CommitResult<UpdateTransaction>> commitListener = new CompletionListener<>();
        txnController.commit(txn, commitListener);
        commitListener.awaitCompletionUninterruptibly();
    }

    protected static String toMillis(final long nanoseconds) {
        return nanoseconds == 0 ? "N/A" : "" + (nanoseconds / 1000000);
    }

}
