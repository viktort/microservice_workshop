/* 
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 */

using System;
using System.Collections.Generic;
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

        public void Forbid(params string[] forbiddenJsonKeys)
        {
            foreach (string key in forbiddenJsonKeys)
            {
                if (IsKeyEmpty(key))
                {
                    _problems.Information("Forbidden key '" + key + "' does not exist");
                }
                else _problems.Error("Forbidden key '" + key + "' actually exists");
            }
        }

        public bool HasKey(string key)
        {
            return _jsonHash[key] != null;
        }

        private bool IsKeyEmpty(string key)
        {
            JToken token = _jsonHash[key];
            return (token == null) ||
                (token.Type == JTokenType.Array && !token.HasValues) ||
                (token.Type == JTokenType.Object && !token.HasValues) ||
                (token.Type == JTokenType.String && token.ToString() == String.Empty) ||
                (token.Type == JTokenType.Null);
        }

        private void AddAccessor(string key)
        {
            if (_recognized_keys.ContainsKey(key)) return;
            switch (_jsonHash[key].Type)   // TODO: Where is polymorphism when you need it?
            {
                case JTokenType.String:
                    _recognized_keys[key] = (string)_jsonHash[key];
                    break;
                case JTokenType.Integer:
                    _recognized_keys[key] = (int)_jsonHash[key];
                    break;
                case JTokenType.Float:
                    _recognized_keys[key] = (float)_jsonHash[key];
                    break;
                default:
                    _recognized_keys[key] = _jsonHash[key];
                    break;
            }
        }

        public object Get(string key)
        {
            return _recognized_keys[key];
        }

        public void Put(string key, object value)
        {
            _recognized_keys[key] = value;
        }
    }
}