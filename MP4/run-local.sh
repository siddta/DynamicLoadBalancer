#!/bin/sh
java -cp "DynamicLoadBalancer-0.0.1-SNAPSHOT-jar-with-dependencies.jar:lib/*" loadbalancer.Main local 0.5 0 http://sp16-cs423-s-g07.cs.illinois.edu:8080/requests 8080 http://sp16-cs423-g07.cs.illinois.edu:8080/requests 8080

