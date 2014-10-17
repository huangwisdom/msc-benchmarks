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
package org.jboss.msc.benchmarks.framework;

/**
 * @author <a href="mailto:ropalka@redhat.com">Richard Opalka</a>
 */
public final class BenchmarksConfig {

    private BenchmarksConfig() {}

    private static final int DEFAULT_SERVICES_COUNT = 1000;
    // TODO: be careful when setting more than 8 threads here - MSC1 has special property for it
    public static final int MSC_THREADS_COUNT = Integer.getInteger("jboss.msc.benchmark.msc.threads.count", 8);
    public static final int INSTALLATION_THREADS_COUNT = Integer.getInteger("jboss.msc.benchmark.installation.threads.count", 8);
    public static final int COMPLETE_GRAPH_SERVICES_COUNT = Integer.getInteger("jboss.msc.benchmark.complete.graph.services.count", DEFAULT_SERVICES_COUNT);
    public static final int LINEAR_GRAPH_SERVICES_COUNT = Integer.getInteger("jboss.msc.benchmark.linear.graph.services.count", DEFAULT_SERVICES_COUNT);
    public static final int DISCRETE_GRAPH_SERVICES_COUNT = Integer.getInteger("jboss.msc.benchmark.discrete.graph.services.count", DEFAULT_SERVICES_COUNT);

}
