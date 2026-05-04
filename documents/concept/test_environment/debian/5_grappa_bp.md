# Setting up Grappa backend plugins in your development environment

This document describes the setup of Grappa backend plugins inside a development environment on a Debian machine.

First, edit `/etc/grappa/grappa-config.yaml` and add the following contents:

```
graders:
  - id:
      name: "DummyGrader"
      version: "1.0"
    display_name: "DummyGrader"
    enabled: true
    grappa_submission_restriction_checks: true
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
      image_name: "ghcr.io/hsh-elc/grappa-backend-dummygrader:latest"

  - id:
      name: "Graja"
      version: "2.3"
    display_name: "Graja2.3"
    proglangs: ["java"]
    enabled: true
    grappa_submission_restriction_checks: true
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

  - id:
      name: "Graflap"
      version: "1.0"
    display_name: "GraFlap"
    proglangs: ["plaintext", "xml"]
    enabled: true
    grappa_submission_restriction_checks: true
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

You can also add more graders. You need to pull the according Docker images for each grader. In this case:

```bash
docker pull ghcr.io/hsh-elc/grappa-backend-dummygrader:latest
docker pull ghcr.io/hsh-elc/grappa-backendplugin-graja:2.3.0.0-develop
docker pull ghcr.io/hsh-elc/grappa-backendplugin-graflap:latest
```

And finally restart Grappa:

```bash
sudo systemctl restart tomcat9
```
