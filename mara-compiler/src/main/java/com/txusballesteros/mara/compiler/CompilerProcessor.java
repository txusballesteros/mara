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
package com.txusballesteros.mara.compiler;

import com.txusballesteros.mara.compiler.compile.TraitComposerCompiler;
import com.txusballesteros.mara.compiler.extractor.TraitComposerExtractor;
import com.txusballesteros.mara.compiler.extractor.TraitComposerModel;
import com.txusballesteros.mara.compiler.extractor.TraitExtractor;
import com.txusballesteros.mara.compiler.extractor.TraitModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({
        "com.txusballesteros.mara.Trait",
        "com.txusballesteros.mara.TraitComposer"
})
public class CompilerProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.size() > 0) {
            Map<String, TraitModel> traits
                    = new TraitExtractor(processingEnv, roundEnv).extract();
            List<TraitComposerModel> composers
                    = new TraitComposerExtractor(processingEnv, roundEnv, traits).extract();
            compileComposers(composers);
            return true;
        } else {
            return false;
        }
    }

    private void compileComposers(List<TraitComposerModel> composers) {
        Filer filer = getFiler();
        for (TraitComposerModel composer : composers) {
            new TraitComposerCompiler(filer)
                                .compile(composer)
                                .generate();
        }
    }

    private Filer getFiler() {
        return processingEnv.getFiler();
    }

    private void writeTrace(String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
    }
}
