build:
	docker-compose build

up:
	docker-compose up -d

down:
	docker-compose down

logs:
	docker-compose logs -f

.PHONY: build up down logs

remove:
	docker-compose down --rmi all --volumes --remove-orphans