# TopTal Test Project

As part of their screening process you have to create a RESTful API that needs to meet all constraints below:

#### Write a REST API that tracks jogging times of users

- API Users must be able to create an account and log in.
- All API calls must be authenticated.
- Implement at least three roles with different permission levels: a regular user would only be able to CRUD on their owned records, a user manager would be able to CRUD only users, and an admin would be able to CRUD all records and users.
- Each time entry when entered has a date, distance, time, and location.
- Based on the provided date and location, API should connect to a weather API provider and get the weather conditions for the run, and store that with each run.
- The API must create a report on average speed & distance per week.
- The API must be able to return data in the JSON format.
- The API should provide filter capabilities for all endpoints that return a list of elements, as well should be able to support pagination.
- The API filtering should allow using parenthesis for defining operations precedence and use any combination of the available fields. The supported operations should at least include or, and, eq (equals), ne (not equals), gt (greater than), lt (lower than).
   Example -> (date eq '2016-05-01') AND ((distance gt 20) OR (distance lt 10)).
- Write unit tests.


Please note that this is the project that will be used to evaluate your skills. The project will be evaluated as if you were delivering it to a customer. We expect you to make sure that the app is fully functional and doesn’t have any obvious missing pieces. The deadline for the project is 2 weeks from today.


### Note

2 weeks was a bit short for me in the evening hours & as someone who is not specialized in building API's. 
So the project is not completed:

Done:
- All API calls must be authenticated.
- Each time entry when entered has a date, distance, time, and location.
- Based on the provided date and location, API should connect to a weather API provider and get the weather conditions for the run, and store that with each run.
- The API must be able to return data in the JSON format.

WIP:
- API Users must be able to create an account and log in. (todo: registration)
- Implement at least three roles with different permission levels: a regular user would only be able to CRUD on their owned records, a user manager would be able to CRUD only users, and an admin would be able to CRUD all records and users. (todo: auth seperation)

Not done:
- The API must create a report on average speed & distance per week.
- The API should provide filter capabilities for all endpoints that return a list of elements, as well should be able to support pagination.
- The API filtering should allow using parenthesis for defining operations precedence and use any combination of the available fields. The supported operations should at least include or, and, eq (equals), ne (not equals), gt (greater than), lt (lower than).
   Example -> (date eq '2016-05-01') AND ((distance gt 20) OR (distance lt 10)).
- Write unit tests.