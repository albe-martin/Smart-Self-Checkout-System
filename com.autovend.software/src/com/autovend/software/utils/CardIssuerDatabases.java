package com.autovend.software.utils;

import com.autovend.external.CardIssuer;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple simulation used just as an example of a membership database.
 */
public class CardIssuerDatabases {
    private CardIssuerDatabases(){}

    /**
     * Just a simple example of a membership database used for validating members
     */
    public static final Map<String, String> MEMBERSHIP_DATABASE = new HashMap<>();
    public static final Map<String, CardIssuer> ISSUER_DATABASE = new HashMap<>();

}
