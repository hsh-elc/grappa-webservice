# Setting up Docker and Redis in your development environment

This document describes the setup of Docker and Redis inside a development environment on a Debian machine.

First, edit the Redis config:

```bash
sudo nano /etc/redis/redis.conf
```

Change two lines:

```
bind 0.0.0.0
requirepass <your Redis password>
```

Next, download and install the official Docker packages on your machine:

```bash
sudo curl -fsSL https://download.docker.com/linux/debian/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc
```

Then edit the apt sources:

```bash
sudo nano /etc/apt/sources.list.d/docker.sources
```

Put:

```
Types: deb
URIs: https://download.docker.com/linux/debian
Suites: trixie
Components: stable
Architectures: amd64
Signed-By: /etc/apt/keyrings/docker.asc
```

Then install the Docker packages

```bash
sudo apt update
sudo apt install docker-ce docker-ce-cli containerd.io
```
