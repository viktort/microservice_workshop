# Copyright (c) 2017 by Fred George.
# May be used freely except for training; license required for training.

require 'json'

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
    packet = packet_from message, packet_problems
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

  private

    def packet_from message, packet_problems
      begin
        Packet.new JSON.parse(message)
      rescue JSON::ParserError
        packet_problems.severe_error("Invalid JSON format. Please check syntax carefully.")
      rescue Exception => e
        packet_problems.severe_error("Packet creation issue:\n\t#{e}")
      end
    end

end
