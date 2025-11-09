#!/bin/bash
#
# Usage: bash startDummyGrader.sh <submission> <response> <image>
#

echo "Running DummyGrader in Docker container ..."

SUBMISSION=$1
RESPONSE=$2
IMAGE=$3
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

if [ -z "$IMAGE" ]; then
  IMAGE="ghcr.io/hsh-elc/grappa-backend-dummygrader:latest"
  echo "Using default image $IMAGE"
fi

if [ -z "$SUBMISSION" ] || [ -z "$RESPONSE" ]; then
  echo "Usage: $0 <submission> <response> <image>"
  exit
fi

SUBMISSION_EXTENSION="${SUBMISSION##*.}"
if [ "$RESPONSE_EXTENSION" != "xml" ] && [ "$SUBMISSION_EXTENSION" != "zip" ]; then
  echo "Submission should end with extension .xml or .zip"
  exit
fi

RESPONSE_EXTENSION="${RESPONSE##*.}"
if [ "$RESPONSE_EXTENSION" != "xml" ] && [ "$RESPONSE_EXTENSION" != "zip" ]; then
  echo "Response should end with extension .xml or .zip"
  exit
fi

if [ ! -f "$SUBMISSION" ]; then
    echo "Error. $SUBMISSION does not exist."
	exit
fi

if [ -e "$RESPONSE" ]; then
    echo "Warning. $RESPONSE exists."
	echo "Overwrite (Y/N)?"
	read ANSWER
    case $ANSWER in
      [yY])  ;;
      *) echo "Abort." && exit ;;
    esac
fi


echo "Check docker daemon ..."
docker version > /dev/null 2>&1
if [ $? -eq 1 ]; then
    echo "Error. Docker daemon not running"
	exit
fi


echo "Creating container ..."

CID=`docker container create --name bpdummygrader -e "TZ=Europe/Berlin" -e SYSPROPS="-Dfile.encoding=UTF-8 -Duser.country=DE -Duser.language=de -Duser.timezone=Europe/Berlin -Dlogging.level=INFO" $IMAGE`

if [ $? -eq 1 ]; then
    echo "Aborting"
	exit
fi

echo "container-id = $CID"

echo "Copying properties file to container ..."
if command -v cygpath > /dev/null 2>&1; then
  PROPERTIES_PATH=$(cygpath -w "$DIR/properties")
else
  PROPERTIES_PATH="$DIR/properties"
fi
docker container cp "$PROPERTIES_PATH" bpdummygrader:/opt/grader/graderBP.properties
echo "v-------- properties ----------v"
cat "$PROPERTIES_PATH"
echo "^-------- properties ----------^"
echo
echo "Copying submission file to container ..."
docker container cp $SUBMISSION bpdummygrader:/var/grb_starter/tmp/submission.$SUBMISSION_EXTENSION

if [ $? -eq 1 ]; then
    echo "Aborting"
	exit
fi

echo "Starting container ..."
docker container start bpdummygrader
if [ $? -eq 1 ]; then
    echo "Aborting"
	exit
fi

echo "Waiting for container to finish ..."
docker container wait bpdummygrader

if [ $? -eq 1 ]; then
    echo "Aborting"
	exit
fi

echo "Copying response file from container ..."
docker container cp bpdummygrader:/var/grb_starter/tmp/response.$RESPONSE_EXTENSION $RESPONSE

if [ $? -eq 1 ]; then
    echo "Aborting"
	exit
fi

echo "Removing container ..."
docker container rm -f bpdummygrader

if [ $? -eq 1 ]; then
    echo "Aborting"
	exit
fi

echo "Finished."
