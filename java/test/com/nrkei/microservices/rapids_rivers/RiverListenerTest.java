package com.nrkei.microservices.rapids_rivers;

import org.junit.Before;
import org.junit.Test;

import static com.sun.xml.internal.ws.dump.LoggingDumpTube.Position.Before;
import static org.junit.Assert.*;

/*
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

// Ensures that River triggers its RiverListeners correctly
public class RiverListenerTest {

    private Rapids rapids;
    private River river;

    @Before
    public void setUp() throws Exception {
        rapids = new TestRapids();
        river = new River(rapids);
    }

    @Test
    public void name() throws Exception {
        river.register(new TestPacketListener () {});
    }

    private class TestRapids extends Rapids {

    }

    private abstract class TestPacketListener implements River.PacketListener {
        @Override
        public void packet(Rapids rapids, Packet packet) {
            fail("Unexpected success parsing JSON packet. Packet is:\n" + packet.toJson());
        }

        @Override
        public void onError(Rapids rapids, PacketProblems problems) {
            fail("Unexpected JSON packet problem(s):\n" + problems.toString());
        }
    }
}