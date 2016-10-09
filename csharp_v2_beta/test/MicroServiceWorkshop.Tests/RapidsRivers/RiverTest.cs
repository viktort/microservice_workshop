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

        [Test]
        public void ValidJson()
        {
            _river.Register(new TestPacketListener((RapidsConnection connection, JObject jsonPacket, PacketProblems warnings) =>
            {
                Assert.False(warnings.HasErrors());
            }));
            _rapidsConnection.Process(SolutionString);
        }

        [Test]
        public void InvalidJson()
        {
            _river.Register(new TestPacketListener((RapidsConnection _rapidsConnection, PacketProblems problems) =>
            {
                Assert.True(problems.HasErrors());
                Assert.That(problems.ToString(), Does.Contain("Invalid JSON format"));
            }));
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

        delegate void Packet(RapidsConnection connection, JObject jsonPacket, PacketProblems warnings);

        delegate void OnError(RapidsConnection connection, PacketProblems errors);

        private class TestPacketListener : River.IPacketListener
        {
            private readonly Packet _successDelegate;
            private readonly OnError _failureDelegate;

            public TestPacketListener(Packet successDelegate) : this(successDelegate, new OnError(UnexpectedFailure)) { }
            public TestPacketListener(OnError failureDelegate) : this(new Packet(UnexpectedSuccess), failureDelegate) { }

            private TestPacketListener(Packet successDelegate, OnError failureDelegate)
            {
                _successDelegate = successDelegate;
                _failureDelegate = failureDelegate;
            }

            public virtual void Packet(RapidsConnection connection, JObject jsonPacket, PacketProblems warnings)
            {
                _successDelegate( connection, jsonPacket, warnings);
            }

            public virtual void OnError(RapidsConnection connection, PacketProblems errors)
            {
                _failureDelegate(connection, errors);
            }

            private static void UnexpectedSuccess(RapidsConnection connection, JObject jsonPacket,
                PacketProblems warnings)
            {
                Assert.Fail("Unexpected successDelegate parsing JSON packet. Packet is:\n"
                            + jsonPacket.ToString()
                            + "\nConclusions from parsing/validation are:\n"
                            + warnings.ToString());
            }

            private static void UnexpectedFailure(RapidsConnection connection, PacketProblems errors)
            {
                Assert.Fail("Unexpected JSON packet problem(s):\n" + errors.ToString());
            }
        }
    }
}