package com.campingmanager.users.entity;

/**
 * Ruoli applicativi. Ognuno corrisponde a un sottotipo di {@link User}
 * e determina i permessi di accesso (ROLE_ADMIN, ROLE_STAFF, ROLE_OSPITE).
 */
public enum Role {
    ADMIN,
    STAFF,
    OSPITE
}
