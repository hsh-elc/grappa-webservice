Get webservice status 
----
Get the service's status information, such as runtime infos.
    
* **URL**

  `/`

* **Method:**
  
  `GET`
  
* **Required URL Params**
 
  *None*

* **HTTP Responses**
  
  * **Code:** `200 OK` <br/>
    **Content Example**:
    ```
    {
        "service": {
            "webappName": "grappa-webservice",
            "staticConfigPath": "/etc/grappa/grappa-config.yaml",
            "totalGradingProcessesExecuted": 0,
            "totalGradingProcessesSucceeded": 0,
            "totalGradingProcessesFailed": 0,
            "totalGradingProcessesCancelled": 0,
            "totalGradingProcessesTimedOut": 0,
            "totalAllExceptExecuted": 0,
            "graderRuntimeInfo": {
                "grader": {
                    "id": "String",
                    "name": "String",
                    "currentlyQueuedSubmissions": 0,
                    "gradingProcessesExecuted": 0,
                    "gradingProcessesSucceeded": 0,
                    "gradingProcessesFailed": 0,
                    "gradingProcessesCancelled": 0,
                    "gradingProcessesTimedOut": 0
                }
            }
        }
    }
    ```    
    **Content Type**: `application/json` <br/>
    **Description**: Returns the status of a grader. This data is subject to change.    
       
  * **Code:** `401 Unauthorized` <br/>
    **Content Example**: `{ error : "String" }` <br/>
    **Content Type**: `application/json` <br/>
    **Description**: Unauthorized access to this resource.

  * **Code:** `500 Internal Server Error` <br/>
    **Content Example**: `{ error : "String" }` <br/>
    **Content Type**: `application/json` <br/>
    **Description**: Unexpected server error.  

Grade a Proforma submission
----
Submit a Proforma submission for grading.

* **URL**

  `/:lmsid/gradeprocesses?graderId=:graderId&async=:async`

* **Method:**
  
  `POST`
  
* **Required URL Params**

  * `lmsid=[string]`: The LMS-ID, which represents the client ID.  
   
  * `graderId=[string]`: The grader instance to be used for grading this submission.
   
  **Optional URL Params**
   
  * `async=[boolean][default=true]`: `true` if this `POST` request should return immediately after submitting the
   Proforma submission for asynchronous grading, or `false` if the `POST` request should block until a Proforma
    response is returned. Note: synchronous (i.e. async=false) grading is not supported yet.   

  * `prioritize=[boolean][default=false]`: `true`, if the submission should be prioritized and graded as soon as a
   grader instance is available, or `false`, if the submission should join the tail of the submission queue. 

* **HTTP Responses**
  
  * **Code:** `201 Created` <br/>
     **Content Example**: `{"gradeProcessId" : "String Id", "estimatedSecondsRemaining": "Integer"}` <br/>
     **Content Type**: `application/json` <br/>
     **Description**: The Proforma submission has been accepted for grading. Use `gradeProcessId` for subsequent polling
      requests. `estimatedSecondsRemaining` indicates the time remaining until the submission is graded.
     
  * **Code:** `400 Bad Request` <br/>
    **Content Example**: `{ error : "String" }` <br/>
    **Content Type**: `application/json` <br/>
    **Description**: The incoming Proforma submission document was ill-formatted or missing required information.
       
  * **Code:** `401 Unauthorized` <br/>
    **Content Example**: `{ error : "String" }` <br/>
    **Content Type**: `application/json` <br/>
    **Description**: Unauthorized access to this resource.

  * **Code:** `404 Not Found` <br/>
    **Content Example**: `{ error : "String" }` <br/>
    **Content Type**: `application/json` <br/>
    **Description**: Parameter `:lmsid` or `:graderId` does not exist.

  * **Code:** `500 Internal Server Error` <br/>
    **Content Example**: `{ error : "String" }` <br/>
    **Content Type**: `application/json` <br/>
    **Description**: Unexpected server error.  
  
Poll for a Proforma response
----
Poll for the status of a Proforma submission (queued for grading, being graded, or already finished).   
    
* **URL**

  `/:lmsid/gradeprocesses/:gradeProcessId`

* **Method:**
  
  `GET`
  
* **Required URL Params**
 
  `lmsid=[string]`
   
  `gradeProcessId=[string]`

* **Required Request Headers**

  * `Accept`=`application/xml` or `multipart/form-data` or`application/octet-stream` <br/>
     **<a name="result-spec-note">Note</a>:** The content type for the `Accept` header should be chosen in
      relation to the `format` attribute specified in the [Proforma submission result-spec element](https://github.com/Proforma/proformaxml/blob/master/Whitepaper.md#the-result-spec-element).
      `application/xml` must be used when the client expects a Proforma `response.xml` file and `multipart/form-data` or`application/octet-stream` for a `response.zip` file.
        

* **HTTP Responses**
  
  * **Code:** `200 OK` <br/>
     **Content Example**: A Proforma Response file named `response.(xml|zip)` <br/>
     **Content Type**: The content depends on the media type of the Proforma response supplied by a grader. In case
      of a Proforma `response.xml` file, Grappa's HTTP response to the client's `GET` request will be of type
       `application
      /xml
      `. If the grader supplies a Proforma `response.zip`, the HTTP response to the client will be of type
       `multipart/form-data` or `application/octet-stream`, depending on what the client is able to accept as
        specified by the `Accept` HTTP header in its request.<br/>
     **Description**: Grading of the submission has finished one way or another. A grading process may either
      finish successfully, be cancelled by a client, or fail for any other technical reason. If the grading process
       finished successfully or failed, a Proforma response for
       the submission will be returned in the HTTP response's content body. Refer to the Proforma response's [is-internal-error flag](https://github.com/Proforma/proformaxml/blob/master/Whitepaper.md#is-internal-error) 
       to determine if the grading process failed. In case the grading process was cancelled by the client, the HTTP
        response's content body will return empty. If the cancellation request is sent in on an already finished
         grading process, the cancellation will have no effect and the response's content body will be a valid Proforma response.   
     
  * **Code:** `202 Accepted` <br/>
    **Content Example**: `{"estimatedSecondsRemaining": "Integer"}` <br/>
    **Description**: The grading process is either pending or in progress. `estimatedSecondsRemaining` indicates the time remaining until the submission is graded.
       
  * **Code:** `401 Unauthorized` <br/>
    **Content Example**: `{ error : "String" }` <br/>
    **Content Type**: `application/json` <br/>
    **Description**: Unauthorized access to this resource.

  * **Code:** `404 Not Found` <br/>
    **Content Example**: `{ error : "String" }` <br/>
    **Content Type**: `application/json` <br/>
    **Description**: Parameter `:lmsid` or `:gradeProcessId` does not exist.
    
  * **Code:** `500 Internal Server Error` <br/>
    **Content Example**: `{ error : "String" }` <br/>
    **Content Type**: `application/json` <br/>
    **Description**: Unexpected server error.  
   
Cancel a Proforma submission 
----
Cancel and delete a Proforma submission that is queued for grading or being currently graded. This request has no
 effect on already graded submissions.
    
* **URL**

  `/:lmsid/gradeprocesses/:gradeProcessId`

* **Method:**
  
  `DELETE`
  
* **Required URL Params**
 
  `lmsid=[string]`
   
  `gradeProcessId=[string]`

* **HTTP Responses**
  
  * **Code:** `200 OK` <br/>
     **Content Example**: 
     **Content Type**: The content type depends on the `Content-Type` in the request header. <br/>
     **Description**: The pending submission has been removed from the submission queue, or the grading process has
      been cancelled if the submission was currently being graded. There will be no Proforma response result for a
       cancelled submisison. <br/>
     
  * **Code:** `202 Accepted` <br/>
    **Content Example**: *No content* <br/>
    **Description**: The grading process is either pending or in progress.
       
  * **Code:** `401 Unauthorized` <br/>
    **Content Example**: `{ error : "String" }` <br/>
    **Content Type**: `application/json` <br/>
    **Description**: Unauthorized access to this resource.

  * **Code:** `404 Not Found` <br/>
    **Content Example**: `{ error : "String" }` <br/>
    **Content Type**: `application/json` <br/>
    **Description**: Parameter `:lmsid` or `:gradeProcessId` does not exist.

  * **Code:** `500 Internal Server Error` <br/>
    **Content Example**: `{ error : "String" }` <br/>
    **Content Type**: `application/json` <br/>
    **Description**: Unexpected server error.  

Get list of all online graders 
----
Get the list of graders that are enabled and online and ready to take on submissions.
    
* **URL**

  `/graders`

* **Method:**
  
  `GET`
  
* **Required URL Params**
 
  *None*

* **HTTP Responses**
  
  * **Code:** `200 OK` <br/>
    **Content Example**:
    ```
    {
        "graders": {
            "graderId0": "String (user-friendly name)"
            "graderId1": "String (user-friendly name)"
        }
    }
    ```
    **Content Type**: `application/json` <br/>
    **Description**: Returns a list of graders that are enabled and online and ready to take on submissions.
       
  * **Code:** `401 Unauthorized` <br/>
    **Content Example**: `{ error : "String" }` <br/>
    **Content Type**: `application/json` <br/>
    **Description**: Unauthorized access to this resource.

  * **Code:** `500 Internal Server Error` <br/>
    **Content Example**: `{ error : "String" }` <br/>
    **Content Type**: `application/json` <br/>
    **Description**: Unexpected server error.  

Get grader status 
----
Get the status, e.g. grader statistics, of a specific grader.
    
* **URL**

  `/graders/:graderId`

* **Method:**
  
  `GET`
  
* **Required URL Params**
 
  `graderId=[string]`

* **HTTP Responses**
  
  * **Code:** `200 OK` <br/>
    **Content Example**:
    ```
    {
        "id": "graderId",
        "name": "human-friendly name",
        "currentlyQueuedSubmissions": 0,
        "gradingProcessesExecuted": 0,
        "gradingProcessesSucceeded": 0,
        "gradingProcessesFailed": 0,
        "gradingProcessesCancelled": 0,
        "gradingProcessesTimedOut": 0
    }
    ```    
    **Content Type**: `application/json` <br/>
    **Description**: Returns the status of a grader. This data is subject to change.    
       
  * **Code:** `401 Unauthorized` <br/>
    **Content Example**: `{ error : "String" }` <br/>
    **Content Type**: `application/json` <br/>
    **Description**: Unauthorized access to this resource.

  * **Code:** `404 Not Found` <br/>
    **Content Example**: `{ error : "String" }` <br/>
    **Content Type**: `application/json` <br/>
    **Description**: Parameter `graderId` does not exist.

  * **Code:** `500 Internal Server Error` <br/>
    **Content Example**: `{ error : "String" }` <br/>
    **Content Type**: `application/json` <br/>
    **Description**: Unexpected server error.  

Check if a Proforma task is chached  
----
Check if a specified task is cached by the middleware to avoid including the task in Proforma
 submissions when doing repeated grading requests for the same Proforma task.
    
* **URL**

  `/tasks/:taskUuid`

* **Method:**
  
  `HEAD`
  
* **Required URL Params**
 
  `taskUuid=[string]`

* **HTTP Responses**
  
  * **Code:** `200 OK` <br/>
    **Content Example**: *None* <br/>
    **Description**: The specified task is cached by the middleware.     
       
  * **Code:** `401 Unauthorized` <br/>
    **Content Example**: `{ error : "String" }` <br/>
    **Content Type**: `application/json` <br/>
    **Description**: Unauthorized access to this resource.

  * **Code:** `404 Not Found` <br/>
    **Content Example**: `{ error : "String" }` <br/>
    **Content Type**: `application/json` <br/>
    **Description**: Parameter `taskUuid` does not exist.

  * **Code:** `500 Internal Server Error` <br/>
    **Content Example**: `{ error : "String" }` <br/>
    **Content Type**: `application/json` <br/>
    **Description**: Unexpected server error.  
