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
package org.jboss.msc.benchmarks.msc1;

import static org.jboss.msc.benchmarks.framework.BenchmarksConfig.*;
import static org.jboss.msc.service.ServiceController.Mode.ACTIVE;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * @author <a href="mailto:frainone@redhat.com">Flavia Rainone</a>
 */
public final class ServiceStartBenchmarkTestCase extends AbstractBenchmarkTest {

    @Test
    public void completeGraph() throws Exception {
        final long nanoseconds = CompleteGraph.benchmark(container, ACTIVE, service, COMPLETE_GRAPH_SERVICES_COUNT, THREADS_COUNT);
        final int servicesCount = COMPLETE_GRAPH_SERVICES_COUNT;
        final String clazz = this.getClass().getName();
        final String method = ".completeGraph()";
        System.out.println(clazz + method + " benchmark execution time: ( services == " + servicesCount + " ) " + toMillis(nanoseconds));
        assertEquals(servicesCount, statistics.getStartCallsCount());
        assertEquals(0, statistics.getStopCallsCount());
        assertFalse(nanoseconds == 0);
    }

    @Test
    public void linearGraph() throws Exception {
        final long nanoseconds = LinearGraph.benchmark(container, ACTIVE, service, LINEAR_GRAPH_SERVICES_COUNT, THREADS_COUNT);
        final int servicesCount = LINEAR_GRAPH_SERVICES_COUNT;
        final String clazz = this.getClass().getName();
        final String method = ".linearGraph()";
        System.out.println(clazz + method + " benchmark execution time: ( services == " + servicesCount + " ) " + toMillis(nanoseconds));
        assertEquals(servicesCount, statistics.getStartCallsCount());
        assertEquals(0, statistics.getStopCallsCount());
        assertFalse(nanoseconds == 0);
    }

    @Test
    public void discreteGraph() throws Exception {
        final long nanoseconds = DiscreteGraph.benchmark(container, ACTIVE,  service,  DISCRETE_GRAPH_SERVICES_COUNT, THREADS_COUNT);
        final int servicesCount = DISCRETE_GRAPH_SERVICES_COUNT;
        final String clazz = this.getClass().getName();
        final String method = ".discreteGraph()";
        System.out.println(clazz + method + " benchmark execution time: ( services == " + servicesCount + " ) " + toMillis(nanoseconds));
        assertEquals(servicesCount, statistics.getStartCallsCount());
        assertEquals(0, statistics.getStopCallsCount());
        assertFalse(nanoseconds == 0);
    }

}
