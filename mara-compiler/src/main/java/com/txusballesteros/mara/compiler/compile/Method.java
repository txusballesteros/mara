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

import com.txusballesteros.mara.compiler.extractor.Clazz;

import java.util.ArrayList;
import java.util.Collection;

public class Method implements Code {
    private Clazz returnedType;
    private String methodName;
    private String accessModifier = PUBLIC_MODIFIER;
    private ArgumentCollection arguments;
    private Collection<Sentence> sentences;

    public void setReturnedType(Clazz returnedType) {
        this.returnedType = returnedType;
    }

    public Collection<Sentence> getSentences() { return sentences; }

    public void addSentence(Sentence sentence) {
        sentences.add(sentence);
    }

    public void addAllSentences(Collection<Sentence> sentences) {
        sentences.addAll(sentences);
    }

    public void addArgument(Argument argument) {
        arguments.add(argument);
    }

    public Method(Clazz returnedType, String methodName) {
        this.arguments = new ArgumentCollection();
        this.sentences = new ArrayList<>();
        this.returnedType = returnedType;
        this.methodName = methodName;
    }

    public Method(String accessModifier, Clazz returnedType, String methodName) {
        this.arguments = new ArgumentCollection();
        this.sentences = new ArrayList<>();
        this.returnedType = returnedType;
        this.methodName = methodName;
        this.accessModifier = accessModifier;
    }

    public Clazz getReturnedType() { return returnedType; }

    public String getSignature() {
        String argumentsValue = "";
        for (Argument argument : arguments) {
            if (!argumentsValue.isEmpty()) {
                argumentsValue += ", ";
            }
            argumentsValue += argument.toString();
        }
        return String.format("%s(%s)", methodName, argumentsValue);
    }

    @Override
    public String toString() {
        return String.format("public %s %s(%s)", returnedType.getClassName(), methodName, arguments.toString());
    }

    @Override
    public void generateCode(String inheritedIndentation, StringBuilder builder) {
        String codeIndentation = inheritedIndentation + CODE_INDENT;
        builder.append(BREAK_SECTION);
        builder.append(codeIndentation);
        builder.append(String.format("%s %s %s(%s) {", accessModifier, returnedType.getClassName(), methodName, arguments.generateCode()));
        for (Sentence sentence : sentences) {
            sentence.generateCode(codeIndentation, builder);
        }
        builder.append(BREAK_LINE);
        builder.append(codeIndentation);
        builder.append("}");
    }
}
