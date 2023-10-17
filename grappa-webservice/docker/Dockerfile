# Dockerfile
# An image for grappa webservice.

# Base is LTS ubuntu
FROM ubuntu:20.04


### Install additional software
# Use `apt-get` in quiet mode and remove caches after to save storage.
RUN apt-get -qq update && apt-get -yq install \
     default-jdk \
     tomcat9 tomcat9-admin \
     nano vim \
     tzdata \
     && rm -rf /var/lib/apt/lists/*

ENV CATALINA_HOME /usr/share/tomcat9
ENV CATALINA_BASE /var/lib/tomcat9
ENV PATH $CATALINA_HOME/bin:$PATH

ADD ["grappa-webservice-2.war",  "$CATALINA_BASE/webapps"]

EXPOSE 8080

CMD ["catalina.sh", "run"]
