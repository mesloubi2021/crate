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

package io.crate.analyze;

import java.util.function.Consumer;

import io.crate.expression.symbol.Symbol;
import io.crate.metadata.RelationName;
import io.crate.sql.tree.ClusteredBy;
import io.crate.sql.tree.CreateBlobTable;

public class AnalyzedCreateBlobTable implements AnalyzedStatement {

    private final RelationName relationName;
    private final CreateBlobTable<Symbol> createBlobTable;

    AnalyzedCreateBlobTable(RelationName relationName,
                            CreateBlobTable<Symbol> createBlobTable) {
        this.relationName = relationName;
        this.createBlobTable = createBlobTable;
    }

    public RelationName relationName() {
        return relationName;
    }

    public CreateBlobTable<Symbol> createBlobTable() {
        return createBlobTable;
    }

    @Override
    public <C, R> R accept(AnalyzedStatementVisitor<C, R> analyzedStatementVisitor, C context) {
        return analyzedStatementVisitor.visitAnalyzedCreateBlobTable(this, context);
    }

    @Override
    public boolean isWriteOperation() {
        return true;
    }

    @Override
    public void visitSymbols(Consumer<? super Symbol> consumer) {
        ClusteredBy<Symbol> clusteredBy = createBlobTable.clusteredBy();
        if (clusteredBy != null) {
            clusteredBy.column().ifPresent(consumer);
            clusteredBy.numberOfShards().ifPresent(consumer);
        }
        createBlobTable.genericProperties().forValues(consumer);
    }
}
