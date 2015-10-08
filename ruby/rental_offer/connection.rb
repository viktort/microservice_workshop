require 'bunny'

class Connection

  def self.with_open(host, port, vhost_name, &block)
    begin
      puts "Opening connection to RabbitMQ host..."
      conn = Bunny.new(
                       user: vhost_name,
                       password: vhost_name,
                       vhost: vhost_name,
                       host: host,
                       port: port,
                       automatically_recover: false)
      conn.start
      channel = conn.create_channel
      exchange = channel.fanout("rapids", durable: true)
      yield(channel, exchange)
    rescue Interrupt
      channel.close
    rescue Exception => ex
      puts ex
    ensure
      conn.close if conn
    end
  end

end
