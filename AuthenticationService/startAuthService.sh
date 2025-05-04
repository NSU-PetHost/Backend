docker build . -t pethostauthservice
docker run --name authService --network pethost-net -p 8080:8080 pethostauthservice:latest   

