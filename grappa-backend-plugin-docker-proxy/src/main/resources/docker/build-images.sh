#!/bin/sh
# Simple script to build all grappa-remote-backend images for Docker
#
# An installation of Docker and a connection to the internet is required.
#
# Usage: ./build-images
# -------------------------------------------------------------------------

# grappa-remote-backend version
GRB_VERSION="v0.1" #unused in image tagging

echo "Building version $GRB_VERSION of grappa-remote-backend images for Docker!"

# Pull latest ubuntu image
echo "Pulling latest ubuntu image..."
docker image pull ubuntu:latest

# Build grappa-remote-backend-base image
echo "Building grappa-remote-backend-base..."
docker build -t grappa-remote-backend-base ./grappa-remote-backend-base

# Build grappa-remote-backend-dummygrader image
echo "Building grappa-remote-backend-dummygrader..."
docker build -t grappa-remote-backend-dummygrader ./grappa-remote-backend-dummygrader

# Clean Docker
docker container prune -f
docker image prune -f
