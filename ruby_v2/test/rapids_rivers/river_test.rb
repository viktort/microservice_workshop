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
    @service.define_singleton_method :packet do |rapids_connection, packet, warnings|
      assert_no_messages warnings
    end
    @rapids_connection.received_message(SOLUTION_STRING)
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
        throw "Unexpected invocation of Service::packet"
      end

      def on_error rapids_connection, errors
        throw "Unexpected invocation of Service::on_error"
      end

      private

        def assert_no_messages packet_problems
          @test.refute packet_problems.messages?, packet_problems.to_s
        end

    end

end
