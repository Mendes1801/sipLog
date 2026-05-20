## Requisitos da infra para subir os containers

### Ao criar a VM
É necessário criar um network docker dentro da VM
```bash
sudo docker network create sip-network
```

### Para subir tudos os serviços pela primeira vez
Para subir todos os serviços pela primeira vez, utilize o comando abaixo.

[!Note] A rede docker já precisa existir

```bash
sudo docker compose -f docker-compose-bds.yml -f docker-compose-keycloak.yml -f docker-compose-spring.yml up -d
```

### Para subir somente os serviços spring (BFF e API_CORE)
Para subir os serviços do BFF e API_CORE, utilize os comandos abaixo.

```bash
sudo docker compose -f docker-compose-spring.yml up -d --build
```
