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

public class Builder implements Code {
    private FieldCollection fields;
    private MethodCollection methods;
    private Method buildMethod;
    private Sentence traitInstancesBlock;

    public Field getField(Clazz type) {
        return fields.get(type.getCannonicalName());
    }

    public Method getBuildMethod() {
        return buildMethod;
    }

    public Sentence getTraitInstancesBlock() {
        return traitInstancesBlock;
    }

    public Builder() {
        traitInstancesBlock = new Sentence(false, "\t//Creating traits instances");
        fields = new FieldCollection();
        methods = new MethodCollection();
        makeFields();
        makeTraitBuilderSetterMethod();
        makeBuildMethod();
    }

    private void makeFields() {
        addField(new Field("TraitBuilder", "traitsBuilder"));
    }

    private void makeTraitBuilderSetterMethod() {
        Method traitBuilderSetterMethod = new Method(new Clazz("", "Builder"), "setBuilder");
        traitBuilderSetterMethod.addArgument(new Argument("TraitBuilder", "traitsBuilder"));
        traitBuilderSetterMethod.addSentence(new Sentence(false, "this.traitsBuilder = traitsBuilder;"));
        traitBuilderSetterMethod.addSentence(new Sentence(true, "this;"));
        addMethod(traitBuilderSetterMethod);
    }

    private void makeBuildMethod() {
        buildMethod = new Method(new Clazz("", "Object"), "build");
        addMethod(buildMethod);
    }

    public boolean addField(Field field) {
        return fields.add(field);
    }

    public void addMethod(Method method) {
        methods.add(method);
    }

    @Override
    public void generateCode(String inheritedIndentation, StringBuilder builder) {
        String codeIndention = inheritedIndentation + CODE_INDENT;
        builder.append(BREAK_SECTION);
        builder.append(codeIndention);
        builder.append("public static class Builder {");
        fields.generateCode(codeIndention, builder);
        methods.generateCode(codeIndention, builder);
        builder.append(BREAK_LINE);
        builder.append(codeIndention);
        builder.append("}");
    }
}
