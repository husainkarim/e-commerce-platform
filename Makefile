up:
	docker compose up -d
down:
	docker compose down
build:
	docker compose build
logs:
	docker compose logs -f
restart:
	docker compose down
	docker compose up -d
remove:
	@read -p "Are you sure you want to remove EVERYTHING? (y/N) " confirm; \
	if [ "$$confirm" = "y" ]; then \
		docker compose down --rmi all --volumes --remove-orphans; \
	else \
		echo "Cancelled."; \
	fi