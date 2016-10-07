/* 
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 */

using System;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace MicroServiceWorkshop.RapidsRivers
{
    // Understands a well-formed message
    public class Packet
    {
        private readonly JObject _jsonHash;
        private readonly PacketProblems _problems;

        public Packet(string jsonString, PacketProblems problems)
        {
            _problems = problems;
            try
            {
                _jsonHash = JObject.Parse(jsonString);
            }
            catch (JsonException e)
            {
                _problems.SevereError("Invalid JSON format per NewtonSoft JSON library");
            }
            catch (Exception e)
            {
                _problems.SevereError("Unknown failure. Message is: " + e.Message);
            }
        }
    }
}