/* 
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 */

using System;
using System.Collections.Generic;
using System.Text;
using RabbitMQ.Client;

namespace MicroServiceWorkshop.RapidsRivers.RabbitMQ
{
    internal class RabbitMqRapids : RapidsConnection
    {
        private const string _exchangeName = "rapids";
        private const string _rabbitMqPubSub = "fanout";
        private readonly ConnectionFactory _factory;
        private IConnection _connection;
        private IModel _channel;
        private readonly string _queueName;

        public RabbitMqRapids(string serviceName, string host, string port)
        {
            _queueName = serviceName + "_" + Guid.NewGuid();
            _factory = new ConnectionFactory {HostName = host, Port = int.Parse(port)};
        }

        public override void Register(IMessageListener listener)
        {
            if (_channel == null) Connect();
            base.Register(listener);
        }

        public override void Publish(string message)
        {
            if (_channel == null) Connect();
            var body = Encoding.UTF8.GetBytes(message);
            _channel.BasicPublish(exchange: _exchangeName, routingKey: "", basicProperties: null, body: body);
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
            _channel.ExchangeDeclare(_exchangeName, _rabbitMqPubSub, true, true,
                new Dictionary<string, object>());
        }
    }
}