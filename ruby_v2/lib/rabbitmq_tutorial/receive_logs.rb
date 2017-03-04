#!/usr/bin/env ruby
# encoding: utf-8

require "bunny"

# TODO: Set IP of host on the next line
conn = Bunny.new(:automatically_recover => false, :host => '192.168.0.165', :port => 5680)
conn.start

ch  = conn.create_channel
x   = ch.fanout("logs")
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
