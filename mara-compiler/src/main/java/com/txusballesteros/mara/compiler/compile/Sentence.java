/*
 * Copyright Txus Ballesteros 2015 (@txusballesteros)
 *
 * This file is part of some open source application.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Contact: Txus Ballesteros <txus.ballesteros@gmail.com>
 */
package com.txusballesteros.mara.compiler.compile;

import java.util.ArrayList;
import java.util.Collection;

public class Sentence implements Code {
    private String sentence;
    private boolean needsReturn;
    private Collection<Sentence> innerSentences;

    public void addSentence(Sentence sentence) {
        innerSentences.add(sentence);
    }

    public Sentence(boolean needsReturn, String sentence) {
        this.innerSentences = new ArrayList<>();
        this.sentence = sentence;
        this.needsReturn = needsReturn;
    }

    @Override
    public void generateCode(String inheritedIndentation, StringBuilder builder) {
        String codeIndention = inheritedIndentation + CODE_INDENT;
        builder.append(BREAK_LINE);
        builder.append(codeIndention);
        if (!needsReturn) {
            builder.append(String.format("%s", sentence));
        } else {
            builder.append(String.format("return %s", sentence));
        }
        for (Sentence innerSentence : innerSentences) {
            innerSentence.generateCode(codeIndention, builder);
        }
    }
}
