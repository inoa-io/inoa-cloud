# INOA Cloud

## Todos

* graalvm
* store secrets for gateway in db (hmac/rsa)

Resources:

* SwaggerUI: [management](https://petstore.swagger.io/?url=http://localhost:8080/openapi/management.yaml) [gateway](https://petstore.swagger.io/?url=http://localhost:8080/openapi/gateway.yaml)
* Micronaut: [routes](http://localhost:8080/endpoints/routes)

## Local usage

Build and start application:

```sh
mvn package && java -jar target/*.jar
```

Create Tenant and gateway

```sh
TENANT=`curl -s http://localhost:8080/tenant -d"{\"name\":\"kokuwa\"}" -H"content-type: application/json" | jq -r .id`
GROUP_1=`curl -s http://localhost:8080/group -d"{\"tenant\":\"$TENANT\",\"name\":\"some-group\"}" -H"content-type: application/json" | jq -r .id`
GROUP_2=`curl -s http://localhost:8080/group -d"{\"tenant\":\"$TENANT\",\"name\":\"other-group\"}" -H"content-type: application/json" | jq -r .id`
GATEWAY=`curl -s http://localhost:8080/gateway -d"{\"tenant\":\"$TENANT\",\"name\":\"test-gateway\",\"groups\":[\"$GROUP_1\",\"$GROUP_2\"]}" -H"content-type: application/json" | jq -r .id`
curl http://localhost:8080/gateway/$GATEWAY -s | jq
```

Get signing keys:

```sh
curl --silent --fail http://localhost:8080/auth/keys | jq
```

Get registry jwt with gateway jwt:

```sh
GATEWAY_JWT_HEADER=`echo -n '{"alg":"HS256","typ":"JWT"}' | base64 -w0`
GATEWAY_JWT_PAYLOAD=`echo -n "{\"iss\":\"$GATEWAY\",\"aud\":\"gateway-registry\",\"exp\":1896134400,\"nbf\":0,\"iat\":0,\"jti\":\"nope\"}" | base64 -w0`
GATEWAY_JWT_SIGNATURE=`echo -n "$GATEWAY_JWT_HEADER.$GATEWAY_JWT_PAYLOAD" | openssl dgst -binary -sha256 -hmac pleaseChangeThisSecretForANewOne | base64 -w0`
GATEWAY_JWT="$GATEWAY_JWT_HEADER.$GATEWAY_JWT_PAYLOAD.$GATEWAY_JWT_SIGNATURE"
RESPONSE=`curl -s http://localhost:8080/auth/token -XPOST --get --data-urlencode "grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer" --data-urlencode "assertion=$GATEWAY_JWT"`
REGISTRY_JWT=`echo $RESPONSE | jq -r .access_token`
echo $RESPONSE | jq
echo $REGISTRY_JWT | cut -d'.' -f2 | base64 -d | jq
```

Get and set properties:

```sh
curl -s http://localhost:8080/gateway/properties -H"Authorization: Bearer $REGISTRY_JWT" | jq
curl -s http://localhost:8080/gateway/properties/os -XPUT -d"none" -H"Authorization: Bearer $REGISTRY_JWT" -H"content-type: text/plain"
curl -s http://localhost:8080/gateway/properties -d'{"os":"archlinux","osversion":"1"}' -H'Authorization: Bearer $REGISTRY_JWT' -H"content-type: application/json" | jq
curl -s http://localhost:8080/gateway/properties -H"Authorization: Bearer $REGISTRY_JWT" | jq
curl http://localhost:8080/gateway/$GATEWAY -s | jq
```
