# Dockerfile
# An image for grappa webservice.

# Base is LTS ubuntu
FROM ubuntu:20.04


### Install additional software
# Use `apt-get` in quiet mode and remove caches after to save storage.
RUN apt-get -qq update && apt-get -yq install \
     default-jdk \
     tomcat9 tomcat9-admin \
     && rm -rf /var/lib/apt/lists/* 

ENV CATALINA_HOME /usr/share/tomcat9
ENV CATALINA_BASE /var/lib/tomcat9
ENV CATALINA_TMPDIR /tmp

EXPOSE 8080

ADD ["grappa-webservice-2.war",  "$CATALINA_BASE/webapps"]

CMD ["/usr/share/tomcat9/bin/catalina.sh", "run"]

# For debugging the container:
#ENTRYPOINT ["tail", "-f", "/dev/null"]