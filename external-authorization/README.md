# External Authorization Service

This service will act
as [istio's external authorization service](https://istio.io/latest/docs/tasks/security/authorization/authz-custom/).
The service will authorize the user with a given JWT token and if the token uis vaild the service will create a new
token for all internal services with additional information's needed.
