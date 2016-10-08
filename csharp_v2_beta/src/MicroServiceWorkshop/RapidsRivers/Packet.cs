/* 
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 */

using System;
using System.Collections.Generic;
using System.Reflection;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace MicroServiceWorkshop.RapidsRivers
{
    // Understands a well-formed message
    public class Packet
    {
        private readonly JObject _jsonHash;
        private readonly PacketProblems _problems;
        private readonly Dictionary<string, object> _recognized_keys = new Dictionary<string, object>();

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

        public void Require(params string[] requiredJsonKeys)
        {
            foreach (string key in requiredJsonKeys)
            {
                if (HasKey(key)) AddAccessor(key);
                else _problems.Error("Missing required key '" + key + "'");
            }
        }

        private bool HasKey(string key)
        {
            return _jsonHash[key].Type != JTokenType.Null;
        }

        private void AddAccessor(string key)
        {
            if (_recognized_keys.ContainsKey(key)) return;
            if (_jsonHash[key].Type == JTokenType.String)
                _recognized_keys[key] = (string) _jsonHash[key];
            else if (_jsonHash[key].Type == JTokenType.Integer)
                _recognized_keys[key] = (int)_jsonHash[key];
            else if (_jsonHash[key].Type == JTokenType.Float)
                _recognized_keys[key] = (float) _jsonHash[key];
            else
                _recognized_keys[key] = _jsonHash[key];
        }

        public object Get(string key)
        {
            return _recognized_keys[key];
        }
    }
}