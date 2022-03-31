# Dockerfile
# An image for the Dummygrader.
# This Dockerfile can be used as template for other java backend-plugins.

# Base is latest grappa-remote-backend-base image
#FROM grappa-backend-base:latest
FROM ghcr.io/kiliandangendorf/grappa-backend-base:latest


### Install additional software
# Use `apt-get` in quiet mode and remove caches after to save storage.
# RUN apt-get -qq update && apt-get -yq install \
#     your-desired-package \
#     && rm -rf /var/lib/apt/lists/* 


### Install grader files from install_files
# All jars within `/opt/grader` will be included into classpath recursively.
# You can either place only one jar-file or content of a whole directory containing several jar-files here.
# ADD ["single-jar-file.jar",  "/opt/grader/single-jar-file.jar"]
ADD ["grader", "/opt/grader"]


### Config backend-starter from baseimage
# Set classname of backend-plugin within `/opt/grader`.
ENV GRAPPA_PLUGIN_GRADER_CLASSNAME="de.hsh.grappa.backendplugin.dummygrader.DummyGrader"

# If you need absolute paths in java classpath as well, you can overwrite default classpath. 
# Note to give `/opt/grader` as well semicolon separated.
# ENV GRAPPA_PLUGIN_GRADER_CLASSPATHES="/opt/grader;/absolute/path/to/lib"

# If you want to include not only jar files (default) into classpath overwrite (semicolon separated):
# ENV GRAPPA_PLUGIN_GRADER_FILEEXTENSION=".jar;.zip;.class"


### End
# Script executed after the container ist started.
# Note: The base image initiates the grading process. 
#     If you use an entrypoint, this will overwrite the one of the base image.
#     If so, make sure to call `/setup.sh` of baseimage at the end of your entrypoint to run backend-starter.
#     E.g. Within `/your_entrypoint.sh` add follwing as the last line: `source /setup.sh`
# ENTRYPOINT ["/bin/bash", "/your_entrypoint.sh"]


