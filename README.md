# resource-management-api
Resource management api with statemachine

## The State Machine
The state machine is a simulation of a resource fulfillment process


## The REST-ful API
The REST API exposes links for receiving events to trigger state transitions for
the state machine. The server either responds with `202 Accepted` if an event was accepted,
or `4XX Unprocessable Entity` if the event was not accepted by the state machine.

### API

- request body  
```json
{
  "name":"John Roy",
  "age":32,
  "contractInfo":"EUROPE",
  "state":"ADDED"
  }
```
- event : [BEGIN-CHECK, APPROVE, UNAPPROVE, ACTIVATE
- state : ADDED, IN_CHECK, APPROVED, ACTIVE

| method | api                   | url                                               | input        |
|--------|-----------------------|---------------------------------------------------|--------------|
| POST   | Add Employee          | http://localhost:8080/api/v1/employees/           | request body |
| PUT    | Update Employee       | http://localhost:8080/api/v1/employees/           | request body |
| GET    | Update Employee       | http://localhost:8080/api/v1/employees/{id}       | id           |
| PUT    | Update Employee State | http://localhost:8080/api/v1/employees/{id}/event | id, event    |


### Current State and Links
```
{
    "_links": {
        "IN-CHECK": {
            "href": "http://localhost:8080/api/v1/employees/1/BEGIN_CHECK"
        }, 
        "APPROVED": {
            "href": "http://localhost:8080/api/v1/employees/1/APPROVE""
        }, 
        "Move back to IN-CHECK": {
            "href": "http://localhost:8080/api/v1/employees/1/UNAPPROVE""
        }, 
        "ACTIVE": {
            "href": "http://localhost:8080/api/v1/employees/1/ACTIVATE""
        }
    }
}
```

##
