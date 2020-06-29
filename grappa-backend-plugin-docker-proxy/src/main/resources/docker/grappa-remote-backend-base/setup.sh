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
chmod 0700 /setup.sh # Remove access from setup skript
chmod -R 0705 /opt/grader # Set rx-access to /opt/grader
#chmod -R 0707 /var/submission # Set rwx-access to /var/submission
chmod -R 0707 /var/grb_starter # Set rwx-access to /var/grb_starter
#chmod +x /opt/grader/bootstrap_grader-backend.sh



sh /opt/grader/bootstrap_grader-backend.sh

#tail -f /dev/null # this is only for debugging into a running contianer. remove for productivity



#echo "Checking if network available..."
#ls -p /sys/class/net/
#
#if [ -e /sys/class/net/eth0 ]
#then
## setup network rules if there are any
#if [ -e /rules.v4 ]
#then
#	echo "Initing iptables support..."
#	chmod 0700 /rules.v4 # Remove access from rules
#    iptables-restore < /rules.v4
#else
#    echo "Iptables support disabled."
#fi
## setup traffic shaping rules if there are any
#if [ -e /traffic_shaping ]
#then
#	echo "Initing traffic shaping support..."
#	chmod 0700 /traffic_shaping # Remove access from rules
#    /traffic_shaping
#else
#    echo "Traffic shaping support disabled."
#fi
#else
#	echo "Networking disabled."
##fi
## start grading with other user if possible
#if [ -e /opt/grader/bootstrap_grader-backend.sh ]
#then
#	echo "Bootstrapping grading process..."
##	su - grader_user -c 'sh -s' <<EOF
##/opt/grader/bootstrap_grader-backend
##EOF
#    sh /opt/grader/bootstrap_grader-backend.sh
#else
#    echo "No bootstrap script found to start grading process."
#fi
#
#
#tail -f /dev/null
