docker rm -f  petHost-authService
docker build . -t pethost-authservice
docker run --name petHost-authService --network pethost-net -p 8080:8080 pethost-authservice:latest

