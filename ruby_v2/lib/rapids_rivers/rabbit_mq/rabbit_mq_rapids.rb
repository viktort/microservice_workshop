# Copyright (c) 2017 by Fred George.
# May be used freely except for training; license required for training.

require 'bunny'

require_relative '../rapids_connection'
require_relative './rabbit_mq_river'

# Understands an event bus based on RabbitMQ
class RabbitMqRapids
  include RapidsConnection

  RAPIDS = 'rapids'

  def initialize(host_ip, port)
    @connection = Bunny.new(
      :host => host_ip,
      :port => port.to_i,
      :automatically_recover => false)
  end

  def publish(packet)
    exchange.publish packet.to_json
  end

  def queue queue_name = ""
    channel.queue(queue_name || "", exclusive: true, auto_delete: true).tap do |queue|
      queue.bind exchange
    end
  end

  private

    def channel
      return @channel if @channel
      @connection.start
      @channel = @connection.create_channel
    end

    def exchange
      @exchange ||= channel.fanout(RAPIDS, durable: true, auto_delete: true)
    end

end
