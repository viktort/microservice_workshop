# Copyright (c) 2017 by Fred George.
# May be used freely except for training; license required for training.

require_relative "../test_helper"

require_relative "../../lib/rapids_rivers/rapids_connection"
require_relative "../../lib/rapids_rivers/river"

class RiverTest < MiniTest::Test
  JSON_STRING = {
      "key1" => "value1",
      "solutions" => [],
      "frequent_renter" => "",
      "visit_count" => 2,
      }.to_json

  def test_required_key_exists
    river = River.new(JSON_STRING)
    river.require 'need'
    assert_equal("car_rental_offer", river.result.need)
    river.result.need = "offer_shown"
    assert_equal("offer_shown", river.result.need)
    assert(river.valid?, 'Existing key not detected')
  end

  def test_missing_required_key
    ["missing", "solutions", "frequent_renter"].each do |required_key|
      factory = PacketBuilder.new(JSON_STRING)
      factory.require required_key
      assert(!factory.valid?, "Missing key '#{required_key}' not detected")
      assert_raises(RuntimeError) { factory.result }
    end
  end

  def test_forbidden_key_missing
    ["missing", "solutions", "frequent_renter"].each do |forbidden_key|
      factory = PacketBuilder.new(JSON_STRING)
      factory.forbid forbidden_key
      assert(factory.valid?, "Key '#{forbidden_key}' unexpectedly detected")
      assert(factory.result.methods.include? forbidden_key.to_sym)
      assert(factory.result.methods.include? "#{forbidden_key}=".to_sym)
    end
  end

  def test_detects_forbidden_key_exists
    factory = PacketBuilder.new(JSON_STRING)
    factory.forbid 'need'
    assert(!factory.valid?, 'Forbidden key "need" not rejected')
    assert_raises(RuntimeError) { factory.result }
  end

  def test_unmodified_json_rendering
    factory = PacketBuilder.new(JSON_STRING)
    expected = JSON.parse(JSON_STRING)
    expected['visit_count'] += 1
    assert_equal(expected, JSON.parse(factory.result.to_json))
  end

  def test_modified_json_rendering
    factory = PacketBuilder.new(JSON_STRING).forbid('solutions')
    factory.result.solutions = ["One", "Two"]
    modified_hash = JSON.parse factory.result.to_json
    assert_equal(["One", "Two"], modified_hash['solutions'])
  end

  def test_creating_json
    packet = PacketBuilder.new.interested_in('need', 'user_id').result
    packet.user_id = 20
    packet.need = 'some need'
    assert_equal(
        {'need' => 'some need', 'user_id' => 20, 'visit_count' => 1},
        JSON.parse(packet.to_json))
  end

  def test_visit_count
    packet = PacketBuilder.new.result
    assert_equal(1, JSON.parse(packet.to_json)['visit_count'])
  end

end
