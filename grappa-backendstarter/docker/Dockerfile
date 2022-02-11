# Dockerfile
# A base image for all specific grader images.
# See Dummygrader image as example/template for specific grader image.

# Base is latest ubuntu image
FROM ubuntu:20.04


### Add new user for grading
RUN ["adduser", "--disabled-password", "--gecos", "", "grader_user"]


### Create directories
# - /opt/grader-backend-starter for backend starter and config
# - /opt/grader for grader executables
# - /var/grb_starter/tmp for sharing submission and response
RUN mkdir -p "/opt/grader-backend-starter" \
    mkdir -p "/opt/grader" \
    mkdir -p "/var/grb_starter/tmp"


### Setup default vars for grappa-grader-backend-starter.properties
# At least `GRAPPA_PLUGIN_GRADER_CLASSNAME` should be overwritten in child image.
# From these values the `grappa-grader-backend-starter.properties` will be generated.
ENV GRAPPA_PLUGIN_GRADER_CLASSNAME="de.hsh.grappa.backendplugin.BackendPlugin" \
    GRAPPA_PLUGIN_GRADER_CLASSPATHES="/opt/grader" \
    GRAPPA_PLUGIN_GRADER_FILEEXTENSION=".jar" \
    GRAPPA_PLUGIN_GRADER_CONFIG="graderBP.properties"


### Install software
# - OpenJDK
# - 'file', 'unzip', 'less', 'dos2unix' for debugging
# - 'gettext-base' for 'envsubst' to prepare properties files from envs
RUN apt-get -qq update && apt-get -yq install \
    default-jdk \
    file unzip less dos2unix \
    tzdata \
    gettext-base \
    && rm -rf /var/lib/apt/lists/* 
#acc. to https://docs.docker.com/develop/develop-images/dockerfile_best-practices/#run


### Add grappa-backend-starter, config-template and bootstrap-script
ADD ["starter",  "/opt/grader-backend-starter"]


### setup script
COPY ["setup.sh",  "/setup.sh"]
# make it executable (if +x is not set, inherited images are not able to run this script)
RUN ["chmod", "+x", "/setup.sh"]


### End
# Command executed after the container ist started
# Use CMD to run `setup.sh` which will run the grader backend-starter.
# (It has to be CMD (not RUN), because CMD only runs after the container gets started.
# compared to that, RUN executes during the build process.)
CMD ["/setup.sh"]
