# Copyright (c) 2017 by Fred George.
# May be used freely except for training; license required for training.

require_relative '../river'

# Understands a filtered message stream based on RabbitMQ
class RabbitMqRiver < River

  alias_method :parent_register, :register
  def register service
    super
    begin
      queue(service).subscribe(:block => true)  do |delivery_info, metadata, payload|
        message @rapids_connection, payload
      end
    rescue Interrupt => _
      @rapids_connection.close
      exit(0)
    end
  end

  private

    def queue service
      @queue ||= @rapids_connection.queue service.service_name
    end

end
