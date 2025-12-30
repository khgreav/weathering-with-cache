.PHONY: redis-docker

redis-docker:
	docker run --rm -p 6379:6379 --name redis-local redis:latest
