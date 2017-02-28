# Starts a Ruby session
docker run --name "workshop_ruby" -it -v c:/Users/dev/src/microservice_workshop/ruby_v2:/workshop -w /workshop fredgeorge/microservice_ruby:latest bash

# Starts RabbitMQ pub/sub demo receive_logs:
docker exec -it workshop_ruby ruby ./lib/rabbitmq_sample/receive_logs.rb

# Starts RabbitMQ pub/sub demo emit_log:
docker exec -it workshop_ruby ruby ./lib/rabbitmq_sample/emit_log.rb
