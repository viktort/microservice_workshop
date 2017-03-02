# Copyright (c) 2017 by Fred George.
# May be used freely except for training; license required for training.

require 'json'

require_relative './rapids_connection'
require_relative './packet'

class River

  def initialize rapids_connection
    rapids_connection.register(self);
    @listening_services = []
    @validations = []
  end

  def message send_port, message
    packet_problems = PacketProblems.new message
    packet = packet_from message, packet_problems
    @listening_services.each do |ls|
      next ls.packet(send_port, packet, packet_problems) unless packet_problems.errors?
      ls.on_error(send_port, packet_problems)
    end
  end

  def register service
    @listening_services << service
  end

  def require *keys
    keys.each do |key|
      @validations << lambda do |json_hash, packet, packet_problems|
        validate_required key, json_hash, packet_problems
        create_accessors key, json_hash, packet
      end
    end
    self
  end

  def forbid *keys
    keys.each do |key|
      @validations << lambda do |json_hash, packet, packet_problems|
        validate_missing key, json_hash, packet_problems
        create_accessors key, json_hash, packet
      end
    end
    self
  end

  private

    def packet_from message, packet_problems
      begin
        json_hash = JSON.parse(message)
        packet = Packet.new json_hash
        @validations.each { |v| v.call json_hash, packet, packet_problems }
        packet
      rescue JSON::ParserError
        packet_problems.severe_error("Invalid JSON format. Please check syntax carefully.")
      rescue Exception => e
        packet_problems.severe_error("Packet creation issue:\n\t#{e}")
      end
    end

    def validate_required key, json_hash, packet_problems
      return packet_problems.error "Missing required key #{key}" unless json_hash[key]
      return packet_problems.error "Empty required key #{key}" unless value?(json_hash[key])
    end

    def validate_missing key, json_hash, packet_problems
      return unless json_hash.key? key
      return unless value?(json_hash[key])
      packet_problems.error "Forbidden key #{key} detected"
    end

    def create_accessors key, json_hash, packet
      packet.used_key key
      establish_variable key, json_hash[key], packet
      define_getter key, packet
      define_setter key, packet
    end

    def establish_variable key, value = nil, packet
      variable = variable(key)
      packet.instance_variable_set variable, value
    end

    def define_getter key, packet
      variable = variable(key)
      packet.define_singleton_method(key.to_sym) do
        instance_variable_get variable
      end
    end

    def define_setter key, packet
      variable = variable(key)
      packet.define_singleton_method((key + '=').to_sym) do |new_value|
        instance_variable_set variable, new_value
      end
    end

    def variable key
      ('@' + key.to_s).to_sym
    end

    def value? value_under_test
      return false if value_under_test.nil?
      return true if value_under_test.kind_of?(Numeric)
      return false if value_under_test == ''
      return false if value_under_test == []
      true
    end

end
