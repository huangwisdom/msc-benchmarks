#!/bin/bash -x

PROPS="$PROPS -DfailIfNoTests=false"

mvn clean install $PROPS

OUT_DIR=benchmark-logs

services=10000
x=$services
for i in 1 2 3 4 5
do
  mkdir -p $OUT_DIR
  for msc_version in msc1 msc2
  do
    for suite_name in CycleDetectionBenchmarkTestCase ServiceStartBenchmarkTestCase
    do
      for test_name in discreteGraph linearGraph completeGraph
      do
        FILE="$msc_version.$suite_name.$test_name"
        touch $OUT_DIR/$FILE
        for iteration_no in 0 1 2 3 4 5 6 7 8 9
        do
          PROPS="$PROPS -Djboss.msc.benchmark.discrete.graph.services.count=$x"
          PROPS="$PROPS -Djboss.msc.benchmark.linear.graph.services.count=$x"
          PROPS="$PROPS -Djboss.msc.benchmark.complete.graph.services.count=$x"
          PROPS="$PROPS -Dtest=**/$msc_version/$suite_name#$test_name"
          mvn test $PROPS 2>$OUT_DIR/$FILE"_$iteration_no.err" | grep "execution time" | tee >> $OUT_DIR/$FILE.out
        done
        cat $OUT_DIR/$FILE.out | awk -F ' ' ' { print $8" "$10 } ' | tee $OUT_DIR/$FILE.report
      done
    done
  done
  # increment services count
  x=$(($x + services))
done

exit 0
