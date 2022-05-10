#!/bin/bash
# Script to directly call the grader backend
#
# This script is executed automatically as the grader_user after container start.
# Special preparation before grader start can be done here.
#
# Usage: This should not be executed manually.
# -------------------------------------------------------------------------

echo "Starting grader backend starter..."
date


echo "substitute environment variables for backend starter"
#substitute environment variables from child container to backend-starter.properties
envsubst < /opt/grader-backend-starter/grappa-grader-backend-starter.properties.tpl > /opt/grader-backend-starter/grappa-grader-backend-starter.properties
echo "generated properties:"
echo "cat /opt/grader/starter/grappa-grader-backend-starter.properties"
cat /opt/grader-backend-starter/grappa-grader-backend-starter.properties


# The SYSPROPS variable comes from the docker backend plugin proxy and has the format
# -Dfile.encoding=val -Duser.country=XX -Duser.language=xx -Duser.timezone=some/where -Dlogging.level=val

echo "java $SYSPROPS"
echo "  -cp /opt/grader-backend-starter/grappa-backendstarter.jar:/opt/grader-backend-starter/proformautil-2-1.jar de.hsh.grappa.backendstarter.GraderBackendStarter"

java $SYSPROPS -cp /opt/grader-backend-starter/grappa-backendstarter.jar:/opt/grader-backend-starter/proformautil-2-1.jar de.hsh.grappa.backendstarter.GraderBackendStarter

