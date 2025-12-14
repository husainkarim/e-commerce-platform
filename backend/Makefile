COMPOSE = docker compose

.PHONY: build up down logs restart remove

build:
	$(COMPOSE) build

up:
	$(COMPOSE) up -d

down:
	$(COMPOSE) down

restart:
	$(COMPOSE) down
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
