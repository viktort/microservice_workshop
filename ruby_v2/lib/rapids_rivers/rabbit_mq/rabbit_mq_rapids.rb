# Copyright (c) 2017 by Fred George.
# May be used freely except for training; license required for training.

require 'bunny'

require_relative '../river'

# Understands an event bus based on RabbitMQ
class RabbitMqRapids

  RAPIDS = 'rapids'

  def initialize(host_ip, port)
    binding.pry
    @connection = Bunny.new(
      :host => host_ip,
      :port => port.to_i,
      :automatically_recover => false)
  end

  def publish(packet)
    exchange.publish packet.to_json
  end

  def register(listener)
    queue.subscribe do |delivery_info, metadata, payload|
      listener.message(self, payload)
    end
  end

  private

    def chann
      return @channel if @channel
      @connection.start
      @channel = @connection.create_channel
    end

    def exchange
      @exchange ||= channel.fanout(RAPIDS, durable: true, auto_delete: true)
    end

    def queue
      return @queue if @queue
      @queue = channel.queue("<un-named>", exclusive: true, auto_delete: true)
      @queue.bind exchange
    end

end
