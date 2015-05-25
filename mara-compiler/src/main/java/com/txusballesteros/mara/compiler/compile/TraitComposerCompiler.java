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

import com.txusballesteros.mara.compiler.exception.MaraCompilerException;
import com.txusballesteros.mara.compiler.extractor.Clazz;
import com.txusballesteros.mara.compiler.extractor.Model;
import com.txusballesteros.mara.compiler.extractor.TraitComposerModel;
import com.txusballesteros.mara.compiler.extractor.TraitModel;

import java.io.BufferedWriter;
import java.io.IOException;
import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;

public class TraitComposerCompiler extends Compiler {
    private TraitComposerModel traitComposer;
    private Class traitCompositerClass;

    public TraitComposerCompiler(Filer filer) {
        super(filer);
    }

    @Override
    public Compiler compile(Model model) {
        this.traitComposer = (TraitComposerModel)model;
        this.traitCompositerClass
                = new Class(traitComposer.clazz.getPackageName(),
                            traitComposer.clazz.getClassName());
        this.traitCompositerClass.getBuilder()
                .getBuildMethod().setReturnedType(new Clazz(traitCompositerClass.getPackageName(),
                                                            traitCompositerClass.getFinalClassName()));
        compileTraits();
        return this;
    }

    private void compileTraits() {
        String instancesNames = "";
        for (TraitModel trait : traitComposer.traits) {
            if (!instancesNames.isEmpty())
                instancesNames += ", ";
            instancesNames += getArgumentName(trait.clazz.getClassName());
            compileTrait(trait);
        }
        Sentence returnBuilderSentence
                = new Sentence(false, String.format("\treturn new %s(traitsBuilder);",
                                                    traitCompositerClass.getFinalClassName()));
        Sentence returnSentence
                = new Sentence(false, String.format("\treturn new %s(%s);",
                                                    traitCompositerClass.getFinalClassName(),
                                                    instancesNames));
        traitCompositerClass.getBuilder()
                .getBuildMethod().addSentence(new Sentence(false, "if (traitsBuilder != null) {"));
        traitCompositerClass.getBuilder()
                .getBuildMethod().addSentence(returnBuilderSentence);
        traitCompositerClass.getBuilder()
                .getBuildMethod().addSentence(new Sentence(false, "} else {"));
        traitCompositerClass.getBuilder().getBuildMethod()
                .addSentence(traitCompositerClass.getBuilder().getTraitInstancesBlock());
        traitCompositerClass.getBuilder()
                .getBuildMethod().addSentence(returnSentence);
        traitCompositerClass.getBuilder()
                .getBuildMethod().addSentence(new Sentence(false, "}"));
    }

    private String getArgumentName(String source) {
        if (source.length() > 1) {
            return Character.toLowerCase(source.charAt(0)) + source.substring(1);
        } else {
            return source;
        }
    }

    private void compileTrait(TraitModel trait) {
        new TraitCompiler(traitCompositerClass).compile(trait);
    }

    @Override
    public void generate() {
        try {
            JavaFileObject javaFile = getFiler()
                                .createSourceFile(traitCompositerClass.getFilnalCannonicalName());
            BufferedWriter buffer = new BufferedWriter(javaFile.openWriter());
            StringBuilder codeBuilder = new StringBuilder();
            traitCompositerClass.generateCode("", codeBuilder);
            buffer.append(codeBuilder.toString());
            buffer.close();
        } catch (IOException error) {
            throw new MaraCompilerException(error);
        }
    }
}
