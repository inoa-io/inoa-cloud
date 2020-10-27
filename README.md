todos:
 * graalvm
 * jib statt dockerfile
 * integrationtests: am liebsten mit kind

Resources:
 * [SwaggerUI](https://petstore.swagger.io?url=http://localhost:8081/openapi/spec.yaml)
 * Micronaut: [routes](http://localhost:8091/endpoint/route)
 
## Local usage

Build and start application:
```
mvn package && java -jar target/*.jar
```

Get signing keys:
```
curl --silent --fail http://localhost:8080/auth/keys | jq
```

Get registry jwt with gateway jwt and get properties:
```
GATEWAY_TOKEN=eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJnYXRld2F5LXJlZ2lzdHJ5IiwibmJmIjo5NDY2ODQ4MDAsImlzcyI6IjAwMDAwMDAwLTAwMDAtMDAwMC0wMDAwLTAwMDAwMDAwMDAwMCIsImV4cCI6NjE4NTQyNzQ4MDAsImlhdCI6OTQ2Njg0ODAwLCJqdGkiOiI1MWI5MTY2Zi01N2Q2LTQ5N2QtOGQ5NC1lY2QzMDhjMWQyMjQifQ.BcjIb8uUtWxVn_stFNtc8SrGkekdo8iqZwOIq7HCSsM
echo $GATEWAY_TOKEN | cut -d'.' -f2 | base64 -d | jq
curl --silent --fail --request POST --get http://localhost:8080/auth/token --data-urlencode "grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer" --data-urlencode "assertion=$GATEWAY_TOKEN" | jq
```

Get registry jwt with gateway jwt and get properties:
```
GATEWAY_TOKEN=eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJnYXRld2F5LXJlZ2lzdHJ5IiwibmJmIjo5NDY2ODQ4MDAsImlzcyI6IjAwMDAwMDAwLTAwMDAtMDAwMC0wMDAwLTAwMDAwMDAwMDAwMCIsImV4cCI6NjE4NTQyNzQ4MDAsImlhdCI6OTQ2Njg0ODAwLCJqdGkiOiI1MWI5MTY2Zi01N2Q2LTQ5N2QtOGQ5NC1lY2QzMDhjMWQyMjQifQ.BcjIb8uUtWxVn_stFNtc8SrGkekdo8iqZwOIq7HCSsM
REGISTRY_TOKEN=`curl --silent --fail --request POST --get http://localhost:8080/auth/token --data-urlencode "grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer" --data-urlencode "assertion=$GATEWAY_TOKEN" | jq -r .access_token`
echo $REGISTRY_TOKEN | cut -d'.' -f2 | base64 -d | jq
curl --silent --fail http://localhost:8080/gateway/properties -H "Authorization: Bearer $REGISTRY_TOKEN" | jq
```
