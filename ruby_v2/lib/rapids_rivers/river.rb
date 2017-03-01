# Copyright (c) 2017 by Fred George.
# May be used freely except for training; license required for training.

require_relative './rapids_connection'
require_relative './packet'

class River

  def initialize(rapids_connection)
    rapids_connection.register(self);
    @listening_services = []
    @validations = []
  end

  def message(send_port, message)
    packet_problems = PacketProblems.new(message)
    packet = Packet.new(JSON.parse message)
    @validations.each { |v| v.validate(message) }
    @listening_services.each do |ls|
      packet_problems.errors? ?
          ls.on_error(send_port, packet_problems) :
          ls.packet(send_port, packet, packet_problems)
    end
  end

  def register(service)
    @listening_services << service
  end

end
