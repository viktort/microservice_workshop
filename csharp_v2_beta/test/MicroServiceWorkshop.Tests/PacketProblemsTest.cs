/* 
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 */

using NUnit.Framework;

namespace MicroServiceWorkshop.Tests
{
    // Ensures that PacketProblems operates correctly
    [TestFixture]
    public class PacketProblemsTest
    {
        private static readonly string ValidJson = "{\"key1\":\"value1\"}";
        private PacketProblems _problems;

        [SetUp]
        public void SetUp()
        {
            _problems = new PacketProblems(ValidJson);
        }

        [Test]
        public void NoProblemsFoundDefault()
        {
            Assert.False(_problems.HasProblems());
        }
    }
}