#!/usr/bin/env ruby
# encoding: utf-8

require "bunny"

# TODO: Set IP of host on the next line
conn = Bunny.new(automatically_recover: false, host: '172.20.10.10', port: 5680)
conn.start

ch   = conn.create_channel
x    = ch.fanout("logs")

msg  = ARGV.empty? ? "Hello World!" : ARGV.join(" ")

x.publish(msg)
puts " [x] Sent #{msg}"

conn.close
