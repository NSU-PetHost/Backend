docker network create pethost-net
docker build -f ContentService/Dockerfile -t pethost-contentservice .
docker rm -f pethost-backend-contentService
docker run -d --name pethost-backend-contentService --network pethost-net -p 8081:8081 --mount type=bind,source=$HOME/.m2,target=/root/.m2 pethost-contentservice

echo "ContentService запускается..."
