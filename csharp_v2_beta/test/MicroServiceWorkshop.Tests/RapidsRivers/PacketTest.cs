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
        private static readonly string SolutionString =
            "{\"need\":\"car_rental_offer\"," +
            "\"user_id\":456," +
            "\"solutions\":[" +
            "{\"offer\":\"15% discount\"}," +
            "{\"offer\":\"500 extra points\"}," +
            "{\"offer\":\"free upgrade\"}" +
            "]," +
            "\"frequent_renter\":\"\"," +
            "\"system.read_count\":2," +
            "\"contributing_services\":[]}";

        private static readonly string MissingComma =
            "{\"frequent_renter\":\"\" \"read_count\":2}";

        [Test]
        public void ValidJson()
        {
            PacketProblems problems = new PacketProblems(SolutionString);
            Packet p = new Packet(SolutionString, problems);
            Assert.False(problems.HasErrors());
        }

        [Test]
        public void InvalidJson()
        {
            PacketProblems problems = new PacketProblems(MissingComma);
            Packet p = new Packet(MissingComma, problems);
            Assert.True(problems.HasErrors());
        }
    }
}