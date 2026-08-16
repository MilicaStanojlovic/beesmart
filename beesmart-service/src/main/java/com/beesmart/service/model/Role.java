package com.beesmart.service.model;

/**
 * Application roles.
 *
 * ADMIN     - manages beekeeper accounts (CRUD), nothing else.
 * BEEKEEPER - uses the knowledge base: diagnosis, cause analysis, CEP monitor, templates.
 */
public enum Role {
    ADMIN,
    BEEKEEPER
}
