install:
	mvn clean install

run:
	docker-compose up --build

clean:
	docker-compose down
