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

public class Class implements Code {
    public static final String CLASS_PREFIX = "Mara_";
    private final Package packageName;
    private final String className;
    private Builder classBuilder;
    private ImportCollection imports;
    private FieldCollection variables;
    private MethodCollection methods;
    private Constructor constructor;
    private Sentence constructorBlock;
    private Constructor builderConstructor;

    public Builder getBuilder() {
        return classBuilder;
    }

    public Constructor getConstructor() {
        return constructor;
    }

    public Sentence getConstructorBlock() { return constructorBlock; }

    public void addImport(Import importValue) {
        imports.add(importValue);
    }

    public void addMethod(Method method) {
        addImport(new Import(method.getReturnedType()));
        methods.add(method);
    }

    public Class(String packageName, String className) {
        constructorBlock = new Sentence(false, "\t\t//Adding injected traits to the builder.");
        classBuilder = new Builder();
        variables = new FieldCollection();
        imports = new ImportCollection();
        methods = new MethodCollection();
        this.packageName = new Package(packageName);
        this.className = className;
        makeImports();
        makeVariables();
        makePrivateConstructor();
        makePublicMethods();
    }

    private void makeImports() {
        imports.add(new Import(new Clazz("java.util", "ArrayList")));
        imports.add(new Import(new Clazz("java.util", "Collection")));
        imports.add(new Import(new Clazz("com.txusballesteros.mara", "TraitBuilder")));
    }

    private void makeVariables() {
        variables.add(new Field("TraitBuilder", "traitsBuilder"));
    }

    private void makePrivateConstructor() {
        constructor = new Constructor(Constructor.PRIVATE_MODIFIER, getFinalClassName());
        constructor.addSentence(new Sentence(false, "this.traitsBuilder = new TraitBuilder() {"));
        constructor.addSentence(new Sentence(false, "\t@Override"));
        constructor.addSentence(new Sentence(false, "\tprotected Collection<Object> onPrepareTraits() {"));
        constructor.addSentence(new Sentence(false, "\t\tCollection<Object> traits = new ArrayList<>();"));
        constructor.addSentence(constructorBlock);
        constructor.addSentence(new Sentence(false, "\t\treturn traits;"));
        constructor.addSentence(new Sentence(false, "\t}"));
        constructor.addSentence(new Sentence(false, "}.build();"));

        builderConstructor = new Constructor(Constructor.PRIVATE_MODIFIER, getFinalClassName());
        builderConstructor.addArgument(new Argument("TraitBuilder", "traitsBuilder"));
        builderConstructor.addSentence(new Sentence(false, "this.traitsBuilder = traitsBuilder.build();"));
    }

    private void makePublicMethods() {
        Method getMethod = new Method(Code.PUBLIC_FINAL_MODIFIER, new Clazz("", "<T> T"), "get");
        getMethod.addArgument(new Argument("Class<T>", "clazz"));
        getMethod.addSentence(new Sentence(true, "(T)traitsBuilder.get(clazz);"));
        methods.add(getMethod);
    }

    @Override
    public void generateCode(String inheritedIndentation, StringBuilder builder) {
        packageName.generateCode(inheritedIndentation, builder);
        imports.generateCode(inheritedIndentation, builder);
        builder.append(BREAK_SECTION);
        builder.append(String.format("public final class %s implements %s {", getFinalClassName(), className));
        variables.generateCode(inheritedIndentation, builder);
        builderConstructor.generateCode(inheritedIndentation, builder);
        constructor.generateCode(inheritedIndentation, builder);
        methods.generateCode(inheritedIndentation, builder);
        classBuilder.generateCode(inheritedIndentation, builder);
        builder.append(BREAK_LINE);
        builder.append("}");
    }

    public String getFinalClassName() {
        return String.format("%s%s", CLASS_PREFIX, className);
    }

    public String getPackageName() {
        return packageName.getPackageName();
    }

    public String getFilnalCannonicalName() {
        return String.format("%s.%s", packageName, getFinalClassName());
    }
}
