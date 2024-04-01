#Makefile for setting up AmuseMate server and MindsDB

.PHONY: help clean run_mindsdb

# Default target
help:
	@echo "Available targets:"
	@echo "  clean       : Clean up temporary files"
	@echo "  run_mindsdb : Run MindsDB Docker container"

# Default value for PROMETHEUS_MULTIPROC_DIR
PROMETHEUS_MULTIPROC_DIR ?= /home
MINDSDB_CONTAINER_NAME := mindsdb_container

run_mindsdb:
	@echo "Starting MindsDB Docker container..."
	docker network create mindsdb_network
	docker run --name mindsdb_container --network mindsdb_network -p 47334:47334 -p 47335:47335 \
	-e PROMETHEUS_MULTIPROC_DIR=$(PROMETHEUS_MULTIPROC_DIR) \
	mindsdb/mindsdb

DOCKERFILE := Dockerfile
DOCKER_IMAGE_NAME := my_setup_image

build_setup:
	@echo "Setup starting."
	docker build -t $(DOCKER_IMAGE_NAME) -f $(DOCKERFILE) .
	docker run -it --name my_setup_container --network mindsdb_network $(DOCKER_IMAGE_NAME) bash -c "python3 setup.py"
	@echo "Setup completed."

clean:
	docker stop my_setup_container || true
	docker rm my_setup_container || true
	docker stop mindsdb_container || true
	docker rm mindsdb_container || true
	docker network rm mindsdb_network