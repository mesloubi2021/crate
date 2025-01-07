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

package io.crate.protocols.postgres.types;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NumericTypeTest extends BasePGTypeTest<BigDecimal> {

    public NumericTypeTest() {
        super(NumericType.INSTANCE);
    }

    private static final List<BigDecimal> TEST_NUMBERS = List.of(
        new BigDecimal(0),
        new BigDecimal("-123"),
        new BigDecimal("12345.1"),
        new BigDecimal("-12.12"),
        new BigDecimal("00123"),
        new BigDecimal("12.123").setScale(2, MathContext.DECIMAL64.getRoundingMode()),
        new BigDecimal("1234.0"),
        new BigDecimal("1234.0000").setScale(1, MathContext.DECIMAL64.getRoundingMode())
    );

    @Test
    public void test_read_and_write_numeric_text_value() {
        for (var expected : TEST_NUMBERS) {
            ByteBuf buffer = Unpooled.buffer();
            try {
                pgType.writeAsText(buffer, expected);
                var actual = pgType.readTextValue(buffer, buffer.readInt());
                assertThat(actual).isEqualTo(expected);
                assertThat(actual.toString()).isEqualTo(expected.toString());
            } finally {
                buffer.release();
            }
        }
    }

    @Test
    public void test_read_and_write_numeric_binary_value() {
        for (var expected : TEST_NUMBERS) {
            ByteBuf buffer = Unpooled.buffer();
            try {
                pgType.writeAsBinary(buffer, expected);
                BigDecimal actual = pgType.readBinaryValue(buffer, buffer.readInt());
                assertThat(actual.scale()).isEqualTo(expected.scale());
                assertThat(actual.toString()).isEqualTo(expected.toString());
            } finally {
                buffer.release();
            }
        }
    }
}
