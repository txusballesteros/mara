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
import com.txusballesteros.mara.compiler.extractor.Model;
import com.txusballesteros.mara.compiler.extractor.TraitModel;

public class TraitCompiler extends Compiler {
    private Class composerClass;
    private TraitModel traitModel;

    public TraitCompiler(Class composerClass) {
        super(null);
        this.composerClass = composerClass;
    }

    @Override
    public Compiler compile(Model model) {
        traitModel = (TraitModel)model;
        String contructorArgumentName = getArgumentName(traitModel.clazz.getClassName());
        composerClass.addImport(new Import(traitModel.clazz));
        composerClass.getConstructor().addArgument(new Argument(traitModel.clazz,
                contructorArgumentName));
        composerClass.getConstructorBlock()
                .addSentence(new Sentence(false, String.format("\ttraits.add(%s);",
                        contructorArgumentName)));
        compileConstructors();
        compileInstance();
        compileMethods();
        return this;
    }

    private void compileInstance() {
        String fieldName = getArgumentName(traitModel.clazz.getClassName());
        String constructorArguments = "";
        for (com.txusballesteros.mara.compiler.extractor.Argument argument : traitModel.constructor.arguments) {
            Field field = composerClass.getBuilder().getField(argument.type);
            if (!constructorArguments.isEmpty())
                constructorArguments += ", ";
            constructorArguments += field.getName();
        }
        Sentence sentence = new Sentence(false,
                String.format("%s %s = new %s(%s);",
                        traitModel.clazz.getClassName(),
                        fieldName,
                        traitModel.clazz.getClassName(),
                        constructorArguments));
        composerClass.getBuilder().getTraitInstancesBlock().addSentence(sentence);
    }

    private String getArgumentName(String source) {
        if (source.length() > 1) {
            return Character.toLowerCase(source.charAt(0)) + source.substring(1);
        } else {
            return source;
        }
    }

    private String getSetterName(String source) {
        if (source.length() > 1) {
            return "set" + Character.toUpperCase(source.charAt(0)) + source.substring(1);
        } else {
            return "set" + source.toUpperCase();
        }
    }

    private void compileConstructors() {
        compileConstructor(traitModel.constructor);
    }

    private void compileConstructor(com.txusballesteros.mara.compiler.extractor.Method constructor) {
        for (com.txusballesteros.mara.compiler.extractor.Argument argument : constructor.arguments) {
            compileConstrutorParameter(argument);
        }
    }

    private void compileConstrutorParameter(com.txusballesteros.mara.compiler.extractor.Argument  argument) {
        String fieldName = argument.name;
        String methodName = getSetterName(argument.name);
        String argumentName = getArgumentName(argument.name);
        boolean fieldAdded = composerClass.getBuilder().addField(new Field(argument.type, fieldName));
        if (fieldAdded) {
            composerClass.addImport(new Import(argument.type));
            com.txusballesteros.mara.compiler.compile.Method setterMethod
                    = new com.txusballesteros.mara.compiler.compile.Method(new Clazz("", "Builder"), methodName);
            setterMethod.addArgument(new Argument(argument.type, argumentName));
            setterMethod.addSentence(new Sentence(false,
                    String.format("this.%s = %s;", fieldName, argumentName)));
            setterMethod.addSentence(new Sentence(true, "this;"));
            composerClass.getBuilder().addMethod(setterMethod);
        }
    }

    private void compileMethods() {
        for (com.txusballesteros.mara.compiler.extractor.Method method : traitModel.methods) {
            Method methodCode = new Method(method.returnedType, method.name);
            String argumentNames = "";
            for (com.txusballesteros.mara.compiler.extractor.Argument argument : method.arguments) {
                if (!argumentNames.isEmpty())
                    argumentNames += ", ";
                argumentNames += argument.name;
                composerClass.addImport(new Import(argument.type));
                methodCode.addArgument(new Argument(argument.type, argument.name));
            }
            boolean needsReturnValue;
            if (method.returnedType.getClassName().equals("void")) {
                needsReturnValue = false;
            } else {
                needsReturnValue = true;
            }
            methodCode.addSentence(new Sentence(needsReturnValue,
                    String.format("get(%s.class).%s(%s);",
                            traitModel.clazz.getClassName(),
                            method.name,
                            argumentNames)));
            composerClass.addMethod(methodCode);
        }
    }

    @Override
    public void generate() { }
}
