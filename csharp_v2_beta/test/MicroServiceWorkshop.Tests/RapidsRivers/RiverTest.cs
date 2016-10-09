/* 
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 */

using System.Runtime.CompilerServices;
using MicroServiceWorkshop.RapidsRivers;
using Newtonsoft.Json.Linq;
using NUnit.Framework;

namespace MicroServiceWorkshop.Tests.RapidsRivers
{
    // Ensures Packets are well-formed or catch construction errors
    [TestFixture]
    public class RiverTest
    {
        private const string SolutionString =
            "{\"need\":\"car_rental_offer\"," +
            "\"user_id\":456," +
            "\"solutions\":[" +
            "{\"offer\":\"15% discount\"}," +
            "{\"offer\":\"500 extra points\"}," +
            "{\"offer\":\"free upgrade\"}" +
            "]," +
            "\"frequent_renter\":\"\"," +
            "\"sample_float_key\":1.25," +
            "\"system.read_count\":2," +
            "\"contributing_services\":[]}";

        private const string MissingComma =
            "{\"frequent_renter\":\"\" \"read_count\":2}";

        private const string NeedKey = "need";
        private const string UserIdKey = "user_id";
        private const string SampleFloatKey = "sample_float_key";
        private const string KeyToBeAdded = "key_to_be_added";
        private const string EmptyArrayKey = "contributing_services";
        private const string InterestingKey = "frequent_renter";
        private const string SolutionsKey = "solutions";
        
        private TestRapidsConnection _rapidsConnection;
        private River _river;

        [SetUp]
        public void SetUp()
        {
            _rapidsConnection = new TestRapidsConnection();
            _river = new River(_rapidsConnection);
            _rapidsConnection.Register(_river);
        }

        private class ValidJson : TestPacketListener
        {
            public override void Packet(RapidsConnection connections, JObject jsonPacket, PacketProblems problems)
            {
                Assert.False(problems.HasErrors());
            }
        }

        [Test]
        public void ValidJsonTest()
        {
            _river.Register(new ValidJson());
            _rapidsConnection.Process(SolutionString);
        }

        private class InvalidJson : TestPacketListener
        {
            public override void OnError(RapidsConnection connections, PacketProblems problems)
            {
                Assert.True(problems.HasErrors());
                Assert.That(problems.ToString(), Does.Contain("Invalid JSON"));
            }
        }

        [Test]
        public void InvalidJsonTest()
        {
            _river.Register(new InvalidJson());
            _rapidsConnection.Process(MissingComma);
        }

        private class TestRapidsConnection : RapidsConnection
        {
            public override void Publish(string message) { }  // Ignore for this test
            internal void Process(string message)
            {
                foreach (IMessageListener l in Listeners) l.HandleMessage(this, message);
            }
        }

        private abstract class TestPacketListener : River.IPacketListener
        {
            public virtual void Packet(RapidsConnection connection, JObject jsonPacket, PacketProblems warnings)
            {
                Assert.Fail("Unexpected success parsing JSON packet. Packet is:\n"
                            + jsonPacket.ToString()
                            + "\nWarnings discovered were:\n"
                            + warnings.ToString());
            }

            public virtual void OnError(RapidsConnection connection, PacketProblems errors)
            {
                Assert.Fail("Unexpected JSON packet problem(s):\n" + errors.ToString());
            }
        }
    }
}