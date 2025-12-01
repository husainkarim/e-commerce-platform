up:
	sudo docker-compose up -d
down:
	sudo docker-compose down
build:
	sudo docker-compose build
logs:
	sudo docker-compose logs -f
restart:
	sudo docker-compose down
	sudo docker-compose up -d
remove:
	@read -p "Are you sure you want to remove EVERYTHING? (y/N) " confirm; \
	if [ "$$confirm" = "y" ]; then \
		sudo docker-compose down --rmi all --volumes --remove-orphans; \
	else \
		echo "Cancelled."; \
	fi