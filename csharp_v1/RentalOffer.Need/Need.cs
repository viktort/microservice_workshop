using System;

using RabbitMQ.Client;

using RentalOffer.Core;

namespace RentalOffer.Need { 

    public class Need {
        
        public static void Main(string[] args) {
            string host = args[0];
            string port = args[1];

            new Connection(host, port).WithOpen(new Need().PublishNeed);
        }

        private void PublishNeed(Connection connection)
        {
            Console.WriteLine(" [*] Publishing need on the bus... To exit press CTRL+C");
            while (true)
            {
                string message = new NeedPacket().ToJson();
                connection.Publish(message);
                Console.WriteLine(" [x] Published {0} on the bus", message);
                System.Threading.Thread.Sleep(5000);
            }
        }

    }

}
