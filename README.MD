### DOCKER DELOY
```bash
cp docker/config.properties.dist docker/config.properties
cp docker-compose.yml.dist docker-compose.yml
```
* fill the config.properties file
* change external port if you want in docker-compose file

#### to start
 ```bash
 docker-compose up -d --build
 ```
 #### to stop
 ```bash
 docker-compose down
 ```