# Copyright (c) 2017 by Fred George.
# May be used freely except for training; license required for training.

require 'json'

class Packet
  # The following keys are reserved for system usage:
  VISIT_COUNT = 'system.read_count'
  CONTRIBUTING_SERVICES = 'contributing_services'

  def initialize(json_hash)
    @json_hash = json_hash
    @read_count = (@json_hash[VISIT_COUNT] || -1) + 1
    @contributing_services = @json_hash[CONTRIBUTING_SERVICES] || []
    @used_keys = [VISIT_COUNT, CONTRIBUTING_SERVICES]
  end

  def used_key(key)
    @used_keys << key
  end

  def contributing_service(service_registration)
    this_service_mark = {}
    service_registration.mark(this_service_mark)
    @contributing_services << [this_service_mark]
  end

  def to_json
    @used_keys.each { |key| @json_hash[key] = instance_variable_get("@#{key}".to_sym) }
    @json_hash.to_json
  end

  def to_s
    "Packet (in JSON): #{self.to_json}"
  end

end
