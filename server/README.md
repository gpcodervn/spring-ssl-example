# spring-ssl-server

This is a server application with mTLS configuration.

## Start app:

> ./gradlew :bootRun

## Test app:

At certs folder, run `curl` command:

> curl --key client.key --cert client.crt https://localhost:8443/server.api/v1/user


## Disable mTLS

### Change config:

- Open file `application.properties`
- Set `server.ssl.client-auth=none`

### Test app:

Open browser: `https://localhost:8443/server.api/v1/user`

- Username: admin
- Password: admin

Or using `curl` command:

> curl --location --request GET 'https://localhost:8443/server.api/v1/user' \
--header 'Authorization: Basic YWRtaW46YWRtaW4=' \
--insecure