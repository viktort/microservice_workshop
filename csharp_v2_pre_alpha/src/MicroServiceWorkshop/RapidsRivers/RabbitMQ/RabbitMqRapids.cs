
using System;
using System.Collections.Generic;
using System.Text;
using RabbitMQ.Client;

namespace MicroServiceWorkshop.RapidsRivers.RabbitMQ
{
    public class RabbitMqRapids : RapidsConnection
    {
        private const string ExchangeName = "rapids";
        private const string RabbitMqPubSub = "fanout";
        private readonly ConnectionFactory _factory;
        private IConnection _connection;
        private IModel _channel;
        private readonly string _queueName;

        public RabbitMqRapids(string serviceName, string host, string port)
        {
            _queueName = serviceName + "_" + Guid.NewGuid();
            _factory = new ConnectionFactory { HostName = host, Port = int.Parse(port) };
        }

        public override void Register(IMessageListener listener)
        {
            if (_channel == null) Connect();
            ConfigureQueue();
            Console.WriteLine(" [*] Waiting for messages. To exit press CTRL+C");
            //consumeMessages(consumer(channel));
            base.Register(listener);
        }

        public override void Publish(string message)
        {
            if (_channel == null) Connect();
            var body = Encoding.UTF8.GetBytes(message);
            _channel.BasicPublish(exchange: ExchangeName, routingKey: "", basicProperties: null, body: body);
        }

        private void Connect()
        {
            EstablishConnectivity();
            DeclareExchange();
        }

        private void EstablishConnectivity()
        {
            _connection = _factory.CreateConnection();
            _channel = _connection.CreateModel();
        }

        private void DeclareExchange()
        {
            _channel.ExchangeDeclare(ExchangeName, RabbitMqPubSub, true, true,
                new Dictionary<string, object>());
        }

        private void ConfigureQueue()
        {
            _channel.QueueDeclare(this._queueName, false, true, true, null);
            _channel.QueueBind(this._queueName, "rapids", "");
        }
    }
}
