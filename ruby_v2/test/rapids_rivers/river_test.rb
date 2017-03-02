# Copyright (c) 2017 by Fred George.
# May be used freely except for training; license required for training.

require 'pry'

require_relative "../test_helper"

require_relative "../../lib/rapids_rivers/river"

class RiverTest < MiniTest::Test
  SOLUTION_STRING =
      "{\"need\":\"car_rental_offer\"," +
        "\"user_id\":456," +
        "\"solutions\":[" +
        "{\"offer\":\"15% discount\"}," +
        "{\"offer\":\"500 extra points\"}," +
        "{\"offer\":\"free upgrade\"}" +
        "]," +
        "\"frequent_renter\":\"\"," +
        "\"system.read_count\":2," +
        "\"contributing_services\":[]}";

  def setup
    @rapids_connection = TestRapids.new
    @river = River.new(@rapids_connection)
    @service = TestService.new(self)
    @river.register(@service)
  end

  def test_json_valid
    @service.define_singleton_method :packet do |send_port, packet, warnings|
      refute_messages warnings
    end
    @rapids_connection.received_message SOLUTION_STRING
  end

  def test_json_invalid
    @service.define_singleton_method :on_error do |send_port, errors|
      assert_errors errors
    end
    @rapids_connection.received_message "{\"key\":value}"
  end

  def test_required_field
    @river.require 'need', 'user_id'
    @service.define_singleton_method :packet do |send_port, packet, warnings|
      refute_messages warnings
      packet.need = packet.need + "_extra"
      packet.user_id = packet.user_id + 14
    end
    @rapids_connection.received_message SOLUTION_STRING
  end

  def test_required_field_missing
    @river.require 'need', 'missing_key'
    @service.define_singleton_method :on_error do |send_port, errors|
      assert_errors errors
    end
    @rapids_connection.received_message SOLUTION_STRING
  end

  def test_forbidden_field
    @river.forbid 'frequent_renter', 'contributing_services'
    @service.define_singleton_method :packet do |send_port, packet, warnings|
      refute_messages warnings
      packet.frequent_renter = 'platinum'
      packet.contributing_services << 'a testing service'
    end
    @rapids_connection.received_message SOLUTION_STRING
  end

  def test_forbidden_field_exists
    @river.forbid 'frequent_renter', 'user_id'
    @service.define_singleton_method :on_error do |send_port, errors|
      assert_errors errors
    end
    @rapids_connection.received_message SOLUTION_STRING
  end

  private

    class TestRapids
      include RapidsConnection
    end

    class TestService

      def initialize test_instance
        @test = test_instance
      end

      def packet rapids_connection, packet, warnings
        throw "Unexpected invocation of Service::packet. Warnings were:\n#{warnings}"
      end

      def on_error rapids_connection, errors
        throw "Unexpected invocation of Service::on_error. Errors detected were:\n#{errors}"
      end

      private

        def refute_messages packet_problems
          @test.refute packet_problems.messages?, packet_problems.to_s
        end

        def assert_errors packet_problems
          @test.assert packet_problems.errors?, packet_problems.to_s
        end

    end

end
