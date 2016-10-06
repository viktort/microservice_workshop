/* 
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 */

namespace MicroServiceWorkshop
{
    // Understands various information about a received JSON packet
    public class PacketProblems
    {
        private readonly string _originalJson;

        public PacketProblems(string originalJson)
        {
            _originalJson = originalJson;
        }

        public bool HasProblems()
        {
            return false;
        }
    }
}