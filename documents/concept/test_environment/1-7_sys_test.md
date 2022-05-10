# System test of a Moodle / Grappa development environment

This document describes the system test of a setup that is described [here](1_setting_up.md).

Currently this description assumes a windows computer running xampp and WSL.

The following description references individual settings described [here](1_setting_up.md#Individual-data).

## System test

Start Apache and MySQL using the xampp control panel.

Navigate to http://{localip}:{apache.port}/moodle und login to Moodle as site admin.

### Create a course

Navigate to site administration > Courses > Add a new Course.
* Name of the course: Grappa
* No end date
* Save and display.

Select the new course. Turn editing on. Select *Participants* on the left menu. Modify the role of the admin user and add the roles *teacher* and *student*. Don't forget to save.

(Alternatively, it has proven useful to have a dedicated teacher user and a dedicated student user in the course. If you want to go that route, then create a teacher user and a student user as so-called *manual accounts* (site administration > Users > Add a new user) and enrol these users in the new course with the respective roles.)

### Create a question

Select the new course. Turn editing on. Select the question bank via the gear symbol. Create a new question of type *MooPT*. Drag a ProFormA compatible zip file into the respective file field and choose *Extract information*. Select the Grader. Then save.

### Create a quiz

Create a quiz and add the new question to the quiz.

### Attempt the quiz

As a student attempt the quiz and submit a solution file. When using deferred feedback, the Grappa server will receive the submission only, after the student clicked *Submit all and finish*. Watch the console log of the Tomcat Server in the Eclipse console and check, if everything is working fine. Here is a positive example:


```
2021-06-23 19:34:29,084 DEBUG d.h.g.r.AllGradeProcessResources - [GraderId: 'Graja2.2']: grade() with async=1 called.
2021-06-23 19:34:29,097 INFO  d.h.g.r.AllGradeProcessResources - [GraderId: Graja2.2] Processing submission: SubmissionResource{content=byte[286093], mimeType=application/zip}
2021-06-23 19:34:30,900 DEBUG d.h.g.c.RedisController - [TaskUuid: '84fc5505-4415-41df-b0d3-9c43aa0c35db']: refreshing timeout for task
2021-06-23 19:34:30,917 DEBUG d.h.g.c.RedisController - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: pushSubmission(): SubmissionResource{content=byte[286093], mimeType=application/zip}
2021-06-23 19:34:31,019 DEBUG d.h.g.c.RedisController - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: new queue size: 1
2021-06-23 19:34:31,021 DEBUG d.h.g.c.RedisController - [GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: getSubmissionQueueIndex() called.
2021-06-23 19:34:31,133 DEBUG d.h.g.r.AllGradeProcessResources - subm to be graded in queue at pos 0
2021-06-23 19:34:31,174 DEBUG d.h.g.s.GraderPool - Grader 'Graja2.2': semaphore aquired, 4 left
2021-06-23 19:34:31,210 DEBUG d.h.g.c.RedisController - [GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: getSubmissionQueueIndex() called.
2021-06-23 19:34:31,283 DEBUG d.h.g.s.GraderPoolManager - [GradeProcId: 3e397efd-fd57-475e-862b-e1d4fd05e694]: submIndex: 0, groupIndex: 1, noGraderFreeToGradeMe: false, estimatedSec: 23
2021-06-23 19:34:31,283 DEBUG d.h.g.c.RedisController - [GraderId: 'Graja2.2']: popSubmission()
2021-06-23 19:34:31,331 INFO  d.h.g.s.GraderPool - [GraderId: 'Graja2.2', GradeProcessId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Starting grading process...
2021-06-23 19:34:31,345 DEBUG d.h.g.DockerProxyBackendPlugin - Entering DockerProxyBackendPlugin.init()...
2021-06-23 19:34:31,352 DEBUG d.h.g.s.GraderPool - GRADE START: 3e397efd-fd57-475e-862b-e1d4fd05e694
2021-06-23 19:34:31,359 DEBUG d.h.g.DockerProxyBackendPlugin - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Entering DockerProxyBackendPlugin.grade()...
2021-06-23 19:34:31,360 INFO  d.h.g.DockerProxyBackendPlugin - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Setting up docker connection to: tcp://localhost:2375
2021-06-23 19:34:31,434 INFO  d.h.g.DockerProxyBackendPlugin - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Pinging docker daemon...
2021-06-23 19:34:33,784 INFO  d.h.g.DockerProxyBackendPlugin - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Ping successful.
2021-06-23 19:34:33,785 INFO  d.h.g.DockerProxyBackendPlugin - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Creating container from image 'grappa-backend-graja-2.2'...
2021-06-23 19:34:33,904 INFO  d.h.g.DockerProxyBackendPlugin - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Container with id '23c9cfb14f04523bba735bdd2f78096546d1641965849d870172a0b4741fc4fa' created
2021-06-23 19:34:33,905 INFO  d.h.g.DockerProxyBackendPlugin - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Copying submission file '/var/grb_starter/tmp/submission.zip' to docker container.
2021-06-23 19:34:34,083 INFO  d.h.g.DockerProxyBackendPlugin - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Starting container...
2021-06-23 19:34:38,020 DEBUG d.h.g.DockerProxyBackendPlugin - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Waiting for the grading process to finish...
2021-06-23 19:34:49,455 DEBUG d.h.g.r.GradeProcessResource - [GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: poll() called.
2021-06-23 19:34:49,490 DEBUG d.h.g.c.RedisController - [GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: getResponse()
2021-06-23 19:34:49,502 DEBUG d.h.g.c.RedisController - [GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: getSubmissionQueueIndex() called.
2021-06-23 19:34:49,540 DEBUG d.h.g.r.GradeProcessResource - [GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Submission is being graded right now.
2021-06-23 19:34:50,896 DEBUG d.h.g.DockerProxyBackendPlugin - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Waiting for the grading process to finish...
2021-06-23 19:34:54,182 DEBUG d.h.g.r.GradeProcessResource - [GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: poll() called.
2021-06-23 19:34:54,217 DEBUG d.h.g.c.RedisController - [GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: getResponse()
2021-06-23 19:34:54,256 DEBUG d.h.g.c.RedisController - [GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: getSubmissionQueueIndex() called.
2021-06-23 19:34:54,362 DEBUG d.h.g.r.GradeProcessResource - [GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Submission is being graded right now.
2021-06-23 19:34:57,715 INFO  d.h.g.DockerProxyBackendPlugin - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Container finished with exit code 0
2021-06-23 19:34:57,716 INFO  d.h.g.DockerProxyBackendPlugin - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Fetching response file: /var/grb_starter/tmp/response.zip
2021-06-23 19:34:58,897 DEBUG d.h.g.r.GradeProcessResource - [GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: poll() called.
2021-06-23 19:34:58,917 DEBUG d.h.g.c.RedisController - [GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: getResponse()
2021-06-23 19:34:58,926 DEBUG d.h.g.c.RedisController - [GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: getSubmissionQueueIndex() called.
2021-06-23 19:34:58,986 DEBUG d.h.g.r.GradeProcessResource - [GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Submission is being graded right now.
2021-06-23 19:34:59,774 INFO  d.h.g.DockerProxyBackendPlugin - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Response file '/var/grb_starter/tmp/response.zip' does not exist.
2021-06-23 19:34:59,774 INFO  d.h.g.DockerProxyBackendPlugin - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Fetching response file: /var/grb_starter/tmp/response.xml
2021-06-23 19:34:59,969 INFO  d.h.g.DockerProxyBackendPlugin - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Fetching container log...
2021-06-23 19:35:01,476 INFO  d.h.g.DockerProxyBackendPlugin - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: [CONTAINER LOG]:
[START] ======================================================
[END] ======================================================
2021-06-23 19:35:01,477 DEBUG d.h.g.DockerProxyBackendPlugin - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Stopping container '23c9cfb14f04523bba735bdd2f78096546d1641965849d870172a0b4741fc4fa'...
2021-06-23 19:35:03,525 WARN  d.h.g.DockerProxyBackendPlugin - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Failed to stop container (it may already have stopped): '23c9cfb14f04523bba735bdd2f78096546d1641965849d870172a0b4741fc4fa'
2021-06-23 19:35:03,526 DEBUG d.h.g.DockerProxyBackendPlugin - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Removing container '23c9cfb14f04523bba735bdd2f78096546d1641965849d870172a0b4741fc4fa'...
2021-06-23 19:35:03,694 DEBUG d.h.g.DockerProxyBackendPlugin - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Container removed: '23c9cfb14f04523bba735bdd2f78096546d1641965849d870172a0b4741fc4fa'
2021-06-23 19:35:03,695 INFO  d.h.g.DockerProxyBackendPlugin - [GraderId: 'Graja2.2', GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: DockerProxyBackendPlugin finished.
2021-06-23 19:35:03,702 INFO  d.h.g.s.GraderPool - [GraderId: 'Graja2.2', GradeProcessId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Grading process exited.
2021-06-23 19:35:03,704 INFO  d.h.g.s.GraderPool - [GraderId: 'Graja2.2', GradeProcessId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Grading process finished successfully.
2021-06-23 19:35:03,704 DEBUG d.h.g.s.GraderPool - GRADE ENDE: 3e397efd-fd57-475e-862b-e1d4fd05e694
2021-06-23 19:35:03,704 DEBUG d.h.g.s.GraderPool - Grader 'Graja2.2': semaphore released, 5 left
2021-06-23 19:35:03,722 DEBUG d.h.g.s.GraderPool - Average grading duration: 24 seconds
2021-06-23 19:35:03,868 DEBUG d.h.g.s.GraderPool - [Grader 'Graja2.2']: Caching response: ResponseResource{content=byte[266633], mimeType=text/xml}
2021-06-23 19:35:03,868 DEBUG d.h.g.c.RedisController - [GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: setResponse(): ResponseResource{content=byte[266633], mimeType=text/xml}
2021-06-23 19:35:03,893 DEBUG d.h.g.c.RedisController - Response with gradeProcId '3e397efd-fd57-475e-862b-e1d4fd05e694' set.
2021-06-23 19:35:03,908 DEBUG d.h.g.s.GraderPool - Grader 'Graja2.2': semaphore aquired, 4 left
2021-06-23 19:35:03,909 DEBUG d.h.g.c.RedisController - [GraderId: 'Graja2.2']: popSubmission()
2021-06-23 19:35:03,921 DEBUG d.h.g.s.GraderPool - [GraderID: 'Graja2.2']: This grader's submission queue is empty.
2021-06-23 19:35:03,921 DEBUG d.h.g.s.GraderPool - Grader 'Graja2.2': Nothing to do here. Semaphore released, 5 left
2021-06-23 19:35:04,108 DEBUG d.h.g.r.GradeProcessResource - [GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: poll() called.
2021-06-23 19:35:04,129 DEBUG d.h.g.c.RedisController - [GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: getResponse()
2021-06-23 19:35:04,153 DEBUG d.h.g.r.GradeProcessResource - [GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: ProformaResponse file is available.
2021-06-23 19:35:04,154 DEBUG d.h.g.r.GradeProcessResource - [GradeProcId: '3e397efd-fd57-475e-862b-e1d4fd05e694']: Returning ProformaResponse as APPLICATION_OCTET_STREAM.
```



