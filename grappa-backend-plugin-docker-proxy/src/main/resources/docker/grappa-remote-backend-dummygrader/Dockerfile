# Dockerfile
# An image for the grader 'graja'.

# Base is latest grappa-remote-backend-base image
FROM grappa-remote-backend-base:latest


### Install additional software
# none


### Install grader files from install_files
# add graja installer
ADD ["DummyGraderGrappaV2.jar",  "/opt/grader/dummy-grader/DummyGraderGrappaV2.jar"]
COPY ["DummyGraderGrappaV2.properties", "/opt/grader/dummy-grader/DummyGraderGrappaV2.properties"]

# keep docker container running so it doesn't shutdown without a script running
# makes debugging the container easier
#CMD tail -f /dev/null

### End
# Command executed after the container ist started
#CMD ["/setup.sh"]
#RUN /setup.sh
ENTRYPOINT ["/bin/bash", "/setup.sh"]

#CMD tail -f /dev/null
