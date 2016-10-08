/* 
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 */

using MicroServiceWorkshop.RapidsRivers;
using NUnit.Framework;

namespace MicroServiceWorkshop.Tests.RapidsRivers
{
    // Ensures Packets are well-formed or catch construction errors
    [TestFixture]
    public class PacketTest
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

        private PacketProblems _problems;
        private Packet _packet;

        [SetUp]
        public void SetUp()
        {
            _problems = new PacketProblems(SolutionString);
            _packet = new Packet(SolutionString, _problems);
        }

        [Test]
        public void ValidJson()
        {
            Assert.False(_problems.HasErrors());
        }

        [Test]
        public void InvalidJson()
        {
            PacketProblems problems = new PacketProblems(MissingComma);
            Packet ignore = new Packet(MissingComma, problems);
            Assert.True(problems.HasErrors());
            Assert.That(problems.ToString(), Does.Contain("Invalid JSON"));
        }

        [Test]
        public void RequiredStringKey()
        {
            _packet.Require(NeedKey);
            Assert.False(_problems.HasErrors());
            Assert.AreEqual("car_rental_offer", _packet.Get(NeedKey));
        }

        [Test]
        public void RequiredIntegerKey()
        {
            _packet.Require(UserIdKey);
            Assert.False(_problems.HasErrors());
            Assert.AreEqual(456, _packet.Get(UserIdKey));
        }

        [Test]
        public void RequiredFloatKey()
        {
            _packet.Require(SampleFloatKey);
            Assert.False(_problems.HasErrors());
            Assert.AreEqual(1.25, _packet.Get(SampleFloatKey));
        }

        [Test]
        public void MultipleRequiredKeys()
        {
            _packet.Require(NeedKey, UserIdKey, SampleFloatKey);
            Assert.False(_problems.HasErrors());
            Assert.AreEqual("car_rental_offer", _packet.Get(NeedKey));
            Assert.AreEqual(456, _packet.Get(UserIdKey));
            Assert.AreEqual(1.25, _packet.Get(SampleFloatKey));
        }

        [Test]
        public void MissingRequiredKey()
        {
            _packet.Require("missing_key");
            Assert.True(_problems.HasErrors());
            Assert.That(_problems.ToString(), Does.Contain("missing_key"));
        }
    }
}