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

import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;

/**
 * @author <a href="mailto:ropalka@redhat.com">Richard Opalka</a>
 *
 */
public final class CountingService implements Service<Void> {

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
