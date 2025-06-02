docker network create pethost-net
docker build -f NotificationService/Dockerfile -t pethost-notificationservice .
docker rm -f pethost-backend-notificationService
docker run -d --name pethost-backend-notificationService \
  --network pethost-net \
  -p 8084:8084 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://pethost-backend-psql:5432/pethostnotificationdb \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  -e SPRING_REDIS_HOST=pethost-backend-redis \
  -e SPRING_REDIS_PORT=6379 \
  -e UPLOAD_DIRECTORY=/app/uploads \
  -v images:/app/uploads \
  pethost-notificationservice

echo "NotificationService запускается..."
