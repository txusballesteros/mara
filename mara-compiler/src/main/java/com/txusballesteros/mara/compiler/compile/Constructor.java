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

public class Constructor implements Code {
    private String name;
    private String accessModifier = PUBLIC_MODIFIER;
    private ArgumentCollection arguments;
    private Collection<Sentence> sentences;

    public void addArgument(Argument argument) {
        arguments.add(argument);
    }

    public void addSentence(Sentence sentence) { sentences.add(sentence); }

    public Constructor(String name) {
        this.arguments = new ArgumentCollection();
        this.sentences = new ArrayList<>();
        this.name = name;
    }

    public Constructor(String accessModifier, String name) {
        this.arguments = new ArgumentCollection();
        this.sentences = new ArrayList<>();
        this.name = name;
        this.accessModifier = accessModifier;
    }

    public Constructor(String name, Collection<Argument> arguments) {
        this.arguments = new ArgumentCollection();
        this.sentences = new ArrayList<>();
        this.name = name;
        this.arguments.addAll(arguments);
    }

    @Override
    public String toString() {
        return String.format("%s %s(%s)", accessModifier, name, arguments.toString());
    }

    @Override
    public void generateCode(String inheritedIndentation, StringBuilder builder) {
        String codeIndention = inheritedIndentation + CODE_INDENT;
        builder.append(BREAK_SECTION);
        builder.append(codeIndention);
        builder.append(String.format("%s %s(%s) {", accessModifier, name, arguments.generateCodeForConstructor()));
        generateSentences(inheritedIndentation, builder);
        builder.append(BREAK_LINE);
        builder.append(codeIndention);
        builder.append("}");
    }

    private void generateSentences(String inheritedIndentation, StringBuilder builder) {
        String codeIndention = inheritedIndentation + CODE_INDENT;
        for (Sentence sentence : sentences) {
            sentence.generateCode(codeIndention, builder);
        }
    }
}
