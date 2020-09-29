## Kademlia DHT System

### Launching docker containers
To start server and client and rebuild the docker image
```bash
docker-compose up --build server client 
```


### Start Server
Attach to the server container
```bash
docker exec -it server bash
```

Run the server program
```bash
java -Xmx2048m -cp target/project2-1.0-SNAPSHOT.jar -Djava.security.policy=rmi.policy edu.rit.cs.Server
```


### Start Client
Attach to the client container
```bash
docker exec -it client bash
```


New agent or client then -
```bash
java -Xmx2048m -cp target/project2-1.0-SNAPSHOT.jar -Djava.security.policy=rmi.policy edu.rit.cs.Client portNumber
```




