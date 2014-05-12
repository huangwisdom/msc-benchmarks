#!/bin/bash

PROPS="$PROPS -Djboss.msc.benchmark.discrete.graph.services.count=1000"
PROPS="$PROPS -Djboss.msc.benchmark.linear.graph.services.count=1000"
PROPS="$PROPS -Djboss.msc.benchmark.complete.graph.services.count=1000"
PROPS="$PROPS -DfailIfNoTests=false"
#export MAVEN_OPTS="$MAVEN_OPTS -agentpath:/home/ropalka/java/profilers/yjp-12.0.6/bin/linux-x86-64/libyjpagent.so"
#PROPS="$PROPS -Dtest=**/msc*/CycleDetectionBenchmarkTestCase#discreteGraph"
#PROPS="$PROPS -Dtest=**/msc*/CycleDetectionBenchmarkTestCase#linearGraph"
#PROPS="$PROPS -Dtest=**/msc*/CycleDetectionBenchmarkTestCase#completeGraph"
#PROPS="$PROPS -Dtest=**/msc*/ServiceStartBenchmarkTestCase#discreteGraph"
#PROPS="$PROPS -Dtest=**/msc*/ServiceStartBenchmarkTestCase#linearGraph"
#PROPS="$PROPS -Dtest=**/msc*/ServiceStartBenchmarkTestCase#completeGraph"

mvn clean install $PROPS

