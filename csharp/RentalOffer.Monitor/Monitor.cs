using System;
using System.Text;

using RabbitMQ.Client;

using RentalOffer.Core;

namespace RentalOffer.Monitor { 

    public class Monitor {
        
        public static void Main(string[] args) {
            string host = args[0];
            string port = args[1];

            new Connection(host, port).WithOpen(new Monitor().MonitorSolutions);
        }

        private void MonitorSolutions(Connection connection) {
            var sub = connection.Subscribe();
            Console.WriteLine(" [*] Waiting for solutions on the bus... To exit press CTRL+C");

            while (true) {
                var e = sub.Next();
                var message = Encoding.UTF8.GetString(e.Body);
                Console.WriteLine(" [x] Received: {0}", message);
            }
        }

    }

}
