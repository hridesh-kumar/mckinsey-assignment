# mckinsey-assignment

Description Develop a Spring Boot application that integrates with a third-party currency exchange API to retrieve real-time exchange rates. The application should calculate the total payable amount for a bill in a specified currency after applying applicable discounts. The application should expose an API endpoint that allows users to submit a bill in one currency and get the payable amount in another currency.

### Microservice
Why? With microservice application is scalable and fault isolation, quick deployment and testing. Fast CI/CD.

#### User service
This service handle user login, give toke and validate token. Token use as jwt.
#### UML
![user service](./images/user-service-uml.png)

#### Billing service
Implements `/api/calculate` endpoint. To access this endpoint need to pass Beerer token as Authorization header. This service calles to discount service to get discounted rate then calls exchange service(used third part api https://v6.exchangerate-api.com) convert discounted amount to target currency as a reponse return calculated amount in target currency.

#### UML
![billing service](./images/billing-service-uml.png)

#### Discount service
This service calculate discount amount as per give criteria then retured discounted amount.

#### UML
![discount service](./images/discount-service-uml.png)

#### Service flow
![discount service](./images/service-flow.png)

#### prerequisites
To run service locally below software required
- Java 17
- Maven
- Sonar (If want to see report on local sonar dashboard)

#### Sonar static checks
Executed sonar check on locally each service, command to run sonar as below

```sh
mvn clean verify sonar:sonar -Dsonar.projectKey=<projectKey> -Dsonar.projectName=<projectName> -Dsonar.host.url=http://localhost:9000 -Dsonar.token=<token>
```

#### Result
![sonar report](./images/sonar-report.png)

#### How to Run and test

Run each service with below command
```sh
mvn spring-boot:run
```

##### Get token with below request
To get token, some user preconfigured: hridesh/hridesh, kumar/kumar
```sh
curl --location 'http://localhost:8000/user/login' \
--header 'Content-Type: application/json' \
--data '{
    "username":"hridesh",
    "password":"hridesh"
}'
```

##### Response
```sh
{
    "jwt": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJocmlkZXNoIiwiaWF0IjoxNzI2OTQ1OTkyLCJleHAiOjE3MjY5NDk1OTJ9.3LVLBBGUs-3u7epXGvZhGx2fqxVuHq2Wngo5emSaVQc"
}
```

##### Call calculate api with below sample request
Use jwt in Beerer token received from previous api
```sh
curl --location 'http://localhost:8081/api/calculate' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjdXN0b21lciIsImlhdCI6MTcyNjkzMDA1NywiZXhwIjoxNzI2OTMzNjU3fQ.5leUaA_mdX4UyXB6Dx-Z07tGR_z5IZ-_QfHMd0JGsn4' \
--data '{
    "userType":"customer",
    "tenure": 3,
    "originalCurrency":"USD",
    "targetCurrency":"INR",
    "items": [
        {
            "name": "T-Shirt",
            "category": "cloth",
            "price": 1000
        },
        {
            "name": "Laptop",
            "category": "eclectroics",
            "price": 500
        }
    ]
}'
```

##### Response
```sh
Your bill amount(INR): 112817.475
```
