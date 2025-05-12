docker network create pethost-net
docker build . -t pethost-contentservice
docker rm -f pethost-backend-contentService
docker run -d --name pethost-backend-contentService --network pethost-net -p 8081:8081 pethost-contentservice

echo "ContentService запускается..."
