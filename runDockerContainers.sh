docker netwotk create pethost-net
cd ./postgres
docker build . -t pethost-postgres
docker run --name pethost-backend-psql -p 5432:5432 --network pethost-net -v pgdata:/var/lib/postgresql/data pethost-postgres
cd ../redis
docker build . -t pethost-redis
docker run --name pethost-backend-redis -p 6379:6379 --network pethost-net -v redisdata:/data pethost-redis
cd ../kafka
docker build . -t pethost-kafka
docker run --name pethost-backend-kafka -p 9092:9092 -p 9093:9093 --network pethost-net --network-alias kafka pethost-kafka
