# Copyright (c) 2017 by Fred George.
# May be used freely except for training; license required for training.

# Understands the protocol to an event bus
module RapidsConnection

  def register(listener)
    listeners << listener
  end

  def received_message(message)
    listeners.each { |listener| listener.message(self, message) }
  end

  def publish(message)
    throw "No implementation to send message: \n\t #{message}"
  end

  private

    def listeners
      @listeners ||= []
    end

end
