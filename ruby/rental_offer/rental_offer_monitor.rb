#!/usr/bin/env ruby
# encoding: utf-8

# Docker run command:
#   docker run --name='workshop_monitor' -it -v /c/Users/fred/src/microservice_workshop/ruby:/workshop -w /workshop/rental_offer fredgeorge/ruby_microservice:latest bash
# To run monitor at prompt:
#   ruby rental_car_monitor.rb 192.168.59.103 bugs

require_relative 'connection'

# Streams rental-offer-related requests to the console
class RentalOfferMonitor

  def initialize(host, port)
    @host = host
    @port = port
  end

  def start
    Connection.with_open(@host, @port) {|ch, ex| monitor_solutions(ch, ex) }
  end

private

  def monitor_solutions(channel, exchange)
    queue = channel.queue("", :exclusive => true)
    queue.bind exchange
    puts " [*] Waiting for solutions on the bus... To exit press CTRL+C"
    queue.subscribe(block: true) do |delivery_info, properties, body|
      puts " [x] Received #{body}"
    end
  end

end

RentalOfferMonitor.new(ARGV.shift, ARGV.shift).start
