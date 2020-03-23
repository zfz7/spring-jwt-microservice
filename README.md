# spring-jwt-microservice
WIP: Goal: Spring/JWT/Docker authentication microservice

## Docker Setup Dev
1. Install Docker and Docker Compose
2. Create Docker DB run from app home

```
 docker run -p 5432:5432 -d --name postgres_db \
        -e POSTGRES_PASSWORD=postgres \
        -e PGDATA=/var/lib/postgresql/data/pgdata \
        -e POSTGRES_USER=postgres \
        -e POSTGRES_DB=spring_jwt \
        -v ~/pgdevdata:/var/lib/postgresql/data  \
            postgres
```

## Docker Deployment
1. Create Docker image
```
 docker build . -t auth-service
```
2. Start postgres and jar
```
docker-compose up -d
```

3. Postgres deployment DB is exposed on localhost port 5431
