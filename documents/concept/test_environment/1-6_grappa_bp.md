# Setting up Grappa Backend plugins for a development environment

This document describes a setup of Grappa Backend plugins in a development environment setup as
described [here](1_setting_up.md).

Currently, there is a description for a setup on a Windows computer, which runs Tomcat inside xampp and which runs
docker on the WSL.

The following description references individual settings described [here](1_setting_up.md#Individual-data).

## Grappa Config 
At first, the entries for the different graders are added to the Grappa Config.

### DummyGrader
Add the following lines into ```C:\etc\grappa\grappa-config.yaml``` below the ```graders: ``` section, so it looks like this: 
```
graders:
  - id:
      name: "DummyGrader"
      version: "1.0"
    display_name: "DummyGrader"
    enabled: true  
    timeout_seconds: 60
    concurrent_grading_processes: 5
    result_spec:
      format: "xml"
      structure: "separate-test-feedback" 
      teacher_feedback_level: "debug"
      student_feedback_level: "info"
    show_stacktrace: true
    file_encoding: UTF-8
    user_language: de
    user_country: DE
    operating_mode: docker_jvm_bp
    docker_jvm_bp:        
      image_name: ghcr.io/hsh-elc/grappa-backend-dummygrader:latest
```

### Graja 2.3
Add the following lines into ```C:\etc\grappa\grappa-config.yaml``` below the ```graders: ``` section, so it looks like this:
```
graders:
  - id: 
      name: "DummyGrader"
      version: "1.0"
  ...
  - id:
      name: "Graja"
      version: "2.3"
    display_name: "Graja2.3"
    proglangs: ["java"]
    enabled: true 
    timeout_seconds: 60
    concurrent_grading_processes: 5
    result_spec:
      format: "xml"
      structure: "separate-test-feedback"
      teacher_feedback_level: "debug"
      student_feedback_level: "info"
    show_stacktrace: true
    file_encoding: UTF-8
    user_language: de
    user_country: DE
    operating_mode: docker_jvm_bp
    docker_jvm_bp:        
      image_name: "ghcr.io/hsh-elc/grappa-backendplugin-graja:2.3.0.0-develop" 
```

### GraFlap
Add the following lines into ```C:\etc\grappa\grappa-config.yaml``` below the ```graders: ``` section, so it looks like this:
```
graders:
  - id: 
      name: "DummyGrader"
      version: "1.0"
  ...
  - id: 
      name: "Graja"
      version: "2.3"
  ...
  - id:
      name: "Graflap"
      version: "1.0"
    display_name: "GraFlap"
    proglangs: ["plaintext", "xml"]
    enabled: true
    timeout_seconds: 60
    concurrent_grading_processes: 5
    result_spec:
      format: "xml"
      structure: "separate-test-feedback"
      teacher_feedback_level: "debug"
      student_feedback_level: "info"
    show_stacktrace: true
    file_encoding: UTF-8
    user_language: de
    user_country: DE
    operating_mode: docker_jvm_bp
    docker_jvm_bp:        
      image_name: "ghcr.io/hsh-elc/grappa-backendplugin-graflap:latest"
```

## Pulling Docker Images
Run the following bash script inside the wsl:
```
#!/bin/bash

file="/mnt/c/etc/grappa/grappa-config.yaml"

if [ ! -f $file ]; then
	echo "Error: The file $file does not exist"
	exit
fi

images=($(awk '$1 ~ "^image_name:" && $2 ~ ".*/.*" {gsub("\r", "", $2); gsub("\"", "", $2); print $2}' $file))

for item in ${images[@]}; do
	echo "docker pull $item"
	docker pull "${item}"
done
echo "done"
```
```
sudo bash pull_images.sh
```

You may need to change the Grappa Config file path in the script.

Check, if the docker images have been pulled successfully:

```
sudo docker image ls
```

You should see a list like this:

```
REPOSITORY                                     TAG                IMAGE ID       CREATED         SIZE
ghcr.io/hsh-elc/grappa-backend-dummygrader     latest             6d748bbb9a1a   4 minutes ago   779MB
ghcr.io/hsh-elc/grappa-backendplugin-graja     2.3.0.0-develop    249db441a45a   4 minutes ago   1.07GB
ghcr.io/hsh-elc/grappa-backendplugin-graflap   latest             8881fde3a039   4 minutes ago   788MB
hello-world                                    latest             d1165f221234   3 months ago    13.3kB
```

### Test

Restart Tomcat and watch the output. There should be lines like these:

```
2023-02-25 16:54:27,252 INFO  d.h.g.s.GraderPool - Using grader 'DummyGrader(1.0)' with 5 concurrent instances in docker_jvm_bp mode.
2023-02-25 16:54:27,254 INFO  d.h.g.s.GraderPool - Using grader 'Graja(2.3)' with 5 concurrent instances in docker_jvm_bp mode.
2023-02-25 16:54:27,254 INFO  d.h.g.s.GraderPool - Using grader 'Graflap(1.0)' with 5 concurrent instances in docker_jvm_bp mode.
```


