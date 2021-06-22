# Dockerfile
# A base image for all specific grader images.

# Base is latest ubuntu image
#FROM ubuntu:18.04
FROM ubuntu:latest


# Add new user for grading
RUN ["adduser", "--disabled-password", "--gecos", "", "grader_user"]

# Create directory for grader files
RUN ["mkdir", "/opt/grader"]
RUN ["mkdir", "/opt/grappa/"]
RUN ["mkdir", "/opt/grappa/plugins"]

# Create directory for submission workspace files
#RUN ["mkdir", "/var/submission"] # ... this directory isn't even used by the grading backend starter

# Create directory for result and log
RUN ["mkdir", "/var/grb_starter"]
RUN ["mkdir", "/var/grb_starter/tmp"]

### Install software
# - OpenJDK
# - 'file', 'unzip', 'less', 'dos2unix' for debugging
RUN apt-get update && apt-get -yq install file unzip less dos2unix default-jdk

# Install iptables
#RUN ["apt-get", "-yq", "install", "iptables"]

# Install iproute2
#RUN ["apt-get", "-yq", "install", "iproute2"]


### setup script
COPY ["setup.sh",  "/setup.sh"]


### Add bootstrap_grader-backend script. This is implicitly called after container start.
COPY ["bootstrap_grader-backend.sh", "/opt/grader/bootstrap_grader-backend.sh"]


### Add grappa-remote-backend-starter
ADD ["starter",  "/opt/grader/starter"]


### End
# Command executed after the container ist started
#CMD ["/setup.sh"]
#RUN /setup.sh
#ENTRYPOINT ["/bin/bash", "/setup.sh"]
