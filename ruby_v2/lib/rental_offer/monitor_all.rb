#!/usr/bin/env ruby
# encoding: utf-8

# Copyright (c) 2017 by Fred George.
# May be used freely except for training; license required for training.

# For debugging...
# require 'pry'
# require 'pry-nav'

require 'securerandom'

require '../rapids_rivers/rabbit_mq/rabbit_mq_rapids'

# Understands the complete stream of messages on an event bus
class MonitorAll
  attr_reader :service_name

  def initialize(host_ip, port)
    rapids_connection = RabbitMqRapids.new(host_ip, port)
    @river = RabbitMqRiver.new(rapids_connection)
    @service_name = 'monitor_all_ruby_' + SecureRandom.uuid
  end

  def start
    puts " [*] Waiting for traffic on RabbitMQ event bus ... To exit press CTRL+C"
    @river.register(self)
  end

  def packet rapids_connection, packet, warnings
    puts " [*] #{warnings}"
  end

  def on_error rapids_connection, errors
    puts " [*] #{errors}"
  end

end

MonitorAll.new(ARGV.shift, ARGV.shift.to_i).start
