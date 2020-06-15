# Grappa REST interface

![Grappa REST Interface](https://github.com/hsh-elc/grappa-webservice/blob/master/documents/concept/images/rest_interface.png "Grappa REST Interface")

# HTTP response status codes

## class GrappaResource

### *[GET]* `getStatus() : String` 

status code | description
--- | ---
`200 OK` | status returned
`401 Unauthorized` | unauthorized request (wrong lms id/password)
`500 Server Error` | unexpected server error

## class AllGradeProcessResource

### *[POST]* `grade(submission : InputStream, graderId : String, async : boolean) : void`

status code | description
--- | ---
`201 Created` | a new `GradingProcessResource` has been created as the submission was accepted
`400 Bad Request` | the incoming submission XML was ill-formatted
`400 Bad Request` | the submission document was missing a required info
`401 Unauthorized` | unauthorized request (wrong lms id/password)
`404 Not Found` | parameter `graderId` does not exist
`500 Server Error` | unexpected server error

## class GradeProcessResource

### *[GET]* `poll(gradeProcessInfoId : String) : Response`

status code | description
--- | ---
`200 OK` | the grade process finished one way or the other (a grade process may either finish, cancel or fail for whatever reason - check the ProFormA response document for flags such as `is-internal-error`)
`202 Accepted` | the specified grade process is either pending or in progress
`404 Not Found` | parameter `gradeProcessInfoId` does not exist
`401 Unauthorized` | unauthorized request (wrong lms id/password)
`500 Server Error` | unexpected server error

### *[DELETE]* `cancel(gradeProcessInfoId : String) : void`

status code | description
--- | ---
`200 OK` | the grading process has been stopped (if it was running) and the corresponding `GradingProcessResource` deleted
`404 Not Found` | the specified `gradeProcessInfoId` does not exist
`401 Unauthorized` | unauthorized request (wrong lms id/password)
`500 Server Error` | unexpected server error


## class AllGraderResources

### *[GET]* `getGraders() : String`

getGraders() returns a JSON string containing all available graders that are connected to a particular Grappa instance (i.e. the GrappaResource). A grader is available when it is online (i.e. it was pinged by the middleware successfully). The JSON data provides a list of pairs ({uniqueGraderName, uniqueGraderId}), which define all available (online) grader instances. 

Example JSON data:
```json
{
    "graders": {
        "Graja 1.8": "id1",
        "Graja 2.0": "id2",
        "aSQLg": "id3",
    }
}
```

status code | description
--- | ---
`200 OK` | returns a list of all available grader types
`401 Unauthorized` | unauthorized request (wrong lms id/password)
`500 Server Error` | unexpected server error

## class GraderResource

### *[GET]* `getStatus(graderId : String) : String`

status code | description
--- | ---
`200 OK` | returns the status
`401 Unauthorized` | unauthorized request (wrong lms id/password)
`404 Not Found` | the specified `graderId` does not exist
`500 Server Error` | unexpected server error

## class TaskResource

### *[HEAD]* `exists(taskuuid : String) : boolean`

status code | description
--- | ---
`200 OK` | the task with the specified uuid is available in Grappa's cache (so there is no need to submit the entire task object along with the submission)
`401 Unauthorized` | unauthorized request (wrong lms id/password)
`404 Not Found` | the task with the specified `taskuuid` is not cached
`500 Server Error` | unexpected server error

# Submissions

![Submitting a submission](https://github.com/hsh-elc/grappa-webservice/blob/master/documents/concept/images/submitting.png "Submitting")
