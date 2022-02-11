#!/bin/sh
# Setup script to bootstrap the grading process
#
# This script is executed at container start.
#
# Usage: This should not be executed manually.
# -------------------------------------------------------------------------

echo "Starting setup..."

# set/repair permissions
echo "Setting permissions..."
chmod o-x /bin/su # Remove x-access from su binary
chmod -R o-s /bin # Remove setuid bit from /bin

chmod -R 0705 /opt/grader # Set rx-access to /opt/grader
chmod -R 0707 /var/grb_starter # Set rwx-access to /var/grb_starter

chmod +x /opt/grader-backend-starter/bootstrap_grader-backend.sh

# start grading with other user if possible
if [ -e /opt/grader-backend-starter/bootstrap_grader-backend.sh ]
then
	echo "Bootstrapping grading process..."
	bash /opt/grader-backend-starter/bootstrap_grader-backend.sh
else
    echo "No bootstrap found to start grading process."
fi
