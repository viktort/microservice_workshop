package com.nrkei.microservices.rental_offer;

/*
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

public interface MessageHandler {
    void handle(String message);
}
