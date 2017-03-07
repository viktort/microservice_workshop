#!/usr/bin/env ruby
# encoding: utf-8

require "bunny"

# TODO: Set IP and port of host on the next lines
host = ARGV.shift || '192.168.1.72'
port = ARGV.shift || 5672
conn = Bunny.new(automatically_recover: false, host: host, port: port)
conn.start

ch   = conn.create_channel
x    = ch.fanout("rapids", durable: true, auto_delete: true)

msg  = ARGV.empty? ? "Hello World!" : ARGV.join(" ")

x.publish(msg)
puts " [x] Sent #{msg}"

conn.close
