#!/bin/sh
# Script to directly call the grader backend
#
# This script is executed automatically as the grader_user after container start.
# Special preparation before grader start can be done here.
#
# Usage: This should not be executed manually.
# -------------------------------------------------------------------------

echo "Starting grader backend starter..."

#java -jar /opt/grader/starter/grappa-grader-backend-starter.jar
java -cp /opt/grader/starter/grappa-grader-backend-starter.jar de.hsh.grappa.GraderBackendStarter


