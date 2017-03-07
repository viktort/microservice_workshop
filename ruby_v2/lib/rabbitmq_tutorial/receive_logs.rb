#!/usr/bin/env ruby
# encoding: utf-8

require 'pry'
require 'pry-nav'
require "bunny"
require_relative '../rapids_rivers/rabbit_mq/rabbit_mq_rapids'

# TODO: Set IP and port of host on the next lines
host = ARGV.shift || '192.168.1.72'
port = ARGV.shift || 5672
binding.pry
conn = Bunny.new(automatically_recover: false, host: host, port: port)
conn.start

ch  = conn.create_channel
x   = ch.fanout("rapids", durable: true, auto_delete: true)
q   = ch.queue("", :exclusive => true)

q.bind(x)

puts " [*] Waiting for logs. To exit press CTRL+C"

begin
  q.subscribe(:block => true) do |delivery_info, properties, body|
    puts " [x] #{body}"
  end
rescue Interrupt => _
  ch.close
  conn.close

  exit(0)
end
