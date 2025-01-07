/*
 * Licensed to Crate.io GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */

package io.crate.auth;

import org.jetbrains.annotations.Nullable;

import io.crate.protocols.postgres.ConnectionProperties;
import io.crate.role.Role;
import io.crate.role.Roles;
import io.crate.role.SecureHash;

public class PasswordAuthenticationMethod implements AuthenticationMethod {

    public static final String NAME = "password";
    private final Roles roles;

    PasswordAuthenticationMethod(Roles roles) {
        this.roles = roles;
    }

    @Nullable
    @Override
    public Role authenticate(Credentials credentials, ConnectionProperties connProperties) {
        var username = credentials.username();
        var password = credentials.password();
        assert username != null : "User name must be not null on password authentication method";
        Role user = roles.findUser(username);
        if (user != null && password != null && !password.isEmpty()) {
            SecureHash secureHash = user.password();
            if (secureHash != null && secureHash.verifyHash(password)) {
                return user;
            }
        }
        throw new RuntimeException("password authentication failed for user \"" + username + "\"");
    }

    @Override
    public String name() {
        return NAME;
    }
}
