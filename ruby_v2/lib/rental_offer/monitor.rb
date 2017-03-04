#!/usr/bin/env ruby
# encoding: utf-8

# Copyright (c) 2017 by Fred George.
# May be used freely except for training; license required for training.

require 'securerandom'
require 'pry'

require '../rapids_rivers/rabbit_mq/rabbit_mq_rapids'

# Understands the complete stream of messages on an event bus
class Monitor
  attr_reader :service_name

  def initialize(host_ip, port)
    rapids_connection = RabbitMqRapids.new(host_ip, port)
    @river = River.new(rapids_connection)
    @service_name = 'monitor_ruby_' + SecureRandom.uuid
  end

  def start
    @river.register(this)
    puts " [*] Waiting for traffic on RabbitMQ event bus ... To exit press CTRL+C"
  end

  def packet rapids_connection, packet, warnings
    puts " [*] #{warnings}"
  end

  def on_error rapids_connection, errors
    puts " [*] #{errors}"
  end

end

Monitor.new(ARGV.shift, ARGV.shift).start
