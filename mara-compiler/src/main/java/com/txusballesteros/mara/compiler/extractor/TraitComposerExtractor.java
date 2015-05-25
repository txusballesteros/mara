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
package com.txusballesteros.mara.compiler.extractor;

import com.txusballesteros.mara.TraitComposer;
import com.txusballesteros.mara.compiler.exception.MaraCompilerException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public class TraitComposerExtractor extends Extractor {

    private final ProcessingEnvironment processingEnvironment;
    private final RoundEnvironment roundEnvironment;
    private final Map<String, TraitModel> traitsCache;

    public TraitComposerExtractor(ProcessingEnvironment processingEnvironment,
                                  RoundEnvironment roundEnvironment,
                                  Map<String, TraitModel> traits) {
        super(processingEnvironment, roundEnvironment);
        this.processingEnvironment = processingEnvironment;
        this.roundEnvironment = roundEnvironment;
        this.traitsCache = traits;
    }

    public List<TraitComposerModel> extract() {
        List<TraitComposerModel> result = new ArrayList<>();
        for (Element traitComposerElement : getElementsAnnotatedWith(TraitComposer.class)) {
            if (traitComposerElement.getKind() == ElementKind.INTERFACE) {
                TraitComposerModel traitComposer = new TraitComposerModel();
                traitComposer.clazz = extractComposerClazz(traitComposerElement);
                traitComposer.traits = extractInjectedTraits(traitComposerElement);
                result.add(traitComposer);
            }
        }
        return result;
    }

    private Clazz extractComposerClazz(Element element) {
        PackageElement packageElement = (PackageElement)element.getEnclosingElement();
        return new Clazz(packageElement.getQualifiedName().toString(), element.getSimpleName().toString());
    }

    private AnnotationMirror getAnnotation(Element element, Class annotationType) {
        AnnotationMirror result = null;
        for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
            if(annotation.getAnnotationType().toString().equals(annotationType.getCanonicalName())) {
                result = annotation;
                break;
            }
        }
        return result;
    }

    private AnnotationValue getAnnotationValue(AnnotationMirror annotation, String field) {
        if (annotation != null) {
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotation.getElementValues().entrySet()) {
                if (entry.getKey().getSimpleName().toString().equals(field)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    private List<TraitModel> extractInjectedTraits(Element element) {
        List<TraitModel> result = new ArrayList<>();
        AnnotationMirror annotation = getAnnotation(element, TraitComposer.class);
        AnnotationValue traitsValue = getAnnotationValue(annotation, "traits");
        List<Object> annotatedValues = (List<Object>)traitsValue.getValue();
        for (Object annotatedValue : annotatedValues) {
            AnnotationValue annotatedType = (AnnotationValue)annotatedValue;
            Clazz traitClazz = extratInjectedTraitClazz(annotatedType);
            if (traitsCache.containsKey(traitClazz.getCannonicalName())) {
                result.add(traitsCache.get(traitClazz.getCannonicalName()));
            } else {
                throw new MaraCompilerException(String.format("Invalid injected trait, you haven't " +
                        "configured the %s class with @Trait annotation.", traitClazz.getCannonicalName()));
            }
        }
        return result;
    }

    private Clazz extratInjectedTraitClazz(AnnotationValue value) {
        TypeElement type = extractTypeElement(value);
        PackageElement packageElement = (PackageElement)type.getEnclosingElement();
        return new Clazz(packageElement.getQualifiedName().toString(),
                         type.getSimpleName().toString());
    }

    private TypeElement extractTypeElement(AnnotationValue value) {
        TypeElement result = null;
        if (value != null) {
            TypeMirror typeMirror = (TypeMirror)value.getValue();
            Types TypeUtils = getProcessingEnvironment().getTypeUtils();
            result = (TypeElement)TypeUtils.asElement(typeMirror);
        }
        return result;
    }
}
