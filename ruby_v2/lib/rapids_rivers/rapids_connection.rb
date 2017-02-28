# Copyright (c) 2017 by Fred George. 
# May be used freely except for training; license required for training.

module RapidsConnection

  def register(listener)
    listeners << listener
  end

  def publish(message)
    listeners.each { |listener| listener.message(self, message) }
  end

  private

    def listeners
      @listeners ||= []
    end

end