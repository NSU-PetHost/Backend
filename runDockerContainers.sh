docker network create pethost-net
cd ./postgres
docker build . -t pethost-postgres
docker rm -f pethost-backend-psql
docker run -d --name pethost-backend-psql -p 5432:5432 --network pethost-net -v pgdata:/var/lib/postgresql/data pethost-postgres &
cd ../redis
docker build . -t pethost-redis
docker rm -f pethost-backend-redis
docker run -d --name pethost-backend-redis -p 6379:6379 --network pethost-net -v redisdata:/data pethost-redis &
cd ../kafka
docker build . -t pethost-kafka
docker rm -f pethost-backend-kafka
docker run -d --name pethost-backend-kafka -p 9092:9092 -p 9093:9093 --network pethost-net --network-alias kafka pethost-kafka
cd ../AuthenticationService
docker build . -t pethost-authservice
docker rm -f  pethost-backend-authService
docker run -d --name pethost-backend-authService --network pethost-net -p 8080:8080 pethost-authservice:latest

echo "Контейнеры запускаются..."
