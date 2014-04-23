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

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jboss.msc.service.ServiceContainer;
import org.jboss.msc.service.ServiceContext;
import org.jboss.msc.service.ServiceRegistry;
import org.jboss.msc.txn.BasicTransaction;
import org.jboss.msc.txn.CommitResult;
import org.jboss.msc.txn.CompletionListener;
import org.jboss.msc.txn.PrepareResult;
import org.jboss.msc.txn.TransactionController;
import org.junit.After;
import org.junit.Before;

/**
 * @author <a href="mailto:ropalka@redhat.com">Richard Opalka</a>
 * @author <a href="mailto:frainone@redhat.com">Flavia Rainone</a>
 *
 */
public class AbstractBenchmarkTest {

    static final int THREADS_COUNT = Integer.getInteger("jboss.msc.benchmark.threads.count", 8);
    static final int COMPLETE_GRAPH_SERVICES_COUNT = Integer.getInteger("jboss.msc.benchmark.complete.graph.services.count", 1001);
    static final int LINEAR_GRAPH_SERVICES_COUNT = Integer.getInteger("jboss.msc.benchmark.linear.graph.services.count", 1000/*1*/);
    static final int DISCRETE_GRAPH_SERVICES_COUNT = Integer.getInteger("jboss.msc.benchmark.discrete.graph.services.count", 100000);

    private static ServiceContainer container;
    static TransactionController txnController;
    static ServiceRegistry registry;
    static ServiceContext context;
    static ThreadPoolExecutor executor;
    static BasicTransaction txn;
    static ServiceInvocationStatistics statistics;

    @Before
    public void setUp() throws Exception {
        txnController = TransactionController.createInstance();
        container = txnController.createServiceContainer();
        registry = container.newRegistry();
        context = txnController.getServiceContext();
        executor = new ThreadPoolExecutor(THREADS_COUNT, THREADS_COUNT, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        txn = txnController.createTransaction(executor);
        statistics = new ServiceInvocationStatistics();
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

    public static void prepareAndCommit(final TransactionController txnController, final BasicTransaction txn) {
        final CompletionListener<PrepareResult<BasicTransaction>> prepareListener = new CompletionListener<>();
        txnController.prepare(txn, prepareListener);
        prepareListener.awaitCompletionUninterruptibly();
        final CompletionListener<CommitResult<BasicTransaction>> commitListener = new CompletionListener<>();
        txnController.commit(txn, commitListener);
        commitListener.awaitCompletionUninterruptibly();
    }
}
