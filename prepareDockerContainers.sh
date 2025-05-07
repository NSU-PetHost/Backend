cd ./postgres/
mv Dockerfile-postgres Dockerfile
echo "Замените данные в /postgres/Dockerfile, чтобы корректно была создана база данных"
cd ../redis
mv Dockerfile-redis Dockerfile
cd ../kafka
mv Dockerfile-kafka Dockerfile
cd

cd ./src/main/resources
mv application.properties.origin application.properties
echo "Замените данные в ./src/main/resources/application.properties, что всё работало корректно(если не заполнено в Dockerfile самого сервиса)"
