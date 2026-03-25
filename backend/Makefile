COMPOSE = docker compose
REGISTRY = nexus:5000

.PHONY: build up down logs restart remove

login:
	@echo "Logging into Nexus Repository..."
	docker login $(REGISTRY)

pull-nexus: login
	$(COMPOSE) pull

build:
	$(COMPOSE) build

pull:
	$(COMPOSE) pull

up:
	$(COMPOSE) up -d

down:
	$(COMPOSE) down

restart:
	$(COMPOSE) down
	$(MAKE) pull-nexus
	$(COMPOSE) up -d

logs:
	$(COMPOSE) logs -f

remove:
	@read -p "Are you sure you want to remove EVERYTHING? (y/N) " confirm; \
	if [ "$$confirm" = "y" ]; then \
		$(COMPOSE) down --rmi all --volumes --remove-orphans; \
	else \
		echo "Cancelled."; \
	fi

images:
	$(COMPOSE) images

status:
	$(COMPOSE) ps

jar:
	bash jar-files.sh
