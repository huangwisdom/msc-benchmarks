#!/bin/bash

PROPS="$PROPS -Djboss.msc.benchmark.discrete.graph.services.count=1000"
PROPS="$PROPS -Djboss.msc.benchmark.linear.graph.services.count=1001"
PROPS="$PROPS -Djboss.msc.benchmark.complete.graph.services.count=1001"
PROPS="$PROPS -Djboss.msc.nonrecursive.cycle.detection=false"
PROPS="$PROPS -DfailIfNoTests=false"
#PROPS="$PROPS -Dtest=**/msc*/CycleDetectionBenchmarkTestCase#cycleDetectionDiscreteGraphAllServicesDown"
#PROPS="$PROPS -Dtest=**/msc*/CycleDetectionBenchmarkTestCase#cycleDetectionLinearGraphAllServicesDown"
#PROPS="$PROPS -Dtest=**/msc*/CycleDetectionBenchmarkTestCase#cycleDetectionCompleteGraphAllServicesDown"

for x in 0 1 2 3 4 5 6 7 8 9
do
  mvn clean install $PROPS 2>test$x.err | tee test$x.out
done

exit 0
