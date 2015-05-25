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

import com.txusballesteros.mara.Trait;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public final class TraitExtractor extends Extractor {
    public TraitExtractor(ProcessingEnvironment processingEnvironment,
                          RoundEnvironment roundEnvironment) {
        super(processingEnvironment, roundEnvironment);
    }

    public Map<String, TraitModel> extract() {
        Map<String, TraitModel> result = new HashMap<>();
        for (Element traitElement : getElementsAnnotatedWith(Trait.class)) {
            TraitModel traitModel = new TraitModel();
            traitModel.clazz = extractClass(traitElement);
            traitModel.constructor = processConstructors(traitModel.clazz.getClassName(), traitElement);
            traitModel.methods = extractMethods(traitElement);
            result.put(traitModel.clazz.getCannonicalName(), traitModel);
        }
        return result;
    }

    private Method processConstructors(String className, Element element) {
        List<Method> constructors = new ArrayList<>();
        for (Element item : element.getEnclosedElements()) {
            if (item instanceof ExecutableElement) {
                ExecutableElement methodElement = (ExecutableElement)item;
                if (isAConstructor(methodElement) && isAPublicMethod(methodElement)) {
                    Method method = new Method();
                    method.returnedType = extractClazzFromType(methodElement.getReturnType());
                    method.isStatic = isAStaticMethod(methodElement);
                    method.name = methodElement.getSimpleName().toString();
                    method.arguments = extractMethodArguments(methodElement);
                    method.name = className;
                    constructors.add(method);
                }
            }
        }
        return constructors.get(0);
    }

    private List<Method> extractMethods(Element element) {
        List<Method> result = new ArrayList<>();
        for (Element item : element.getEnclosedElements()) {
            if (item instanceof ExecutableElement) {
                ExecutableElement methodElement = (ExecutableElement)item;
                if (!isAConstructor(methodElement) && isAPublicMethod(methodElement)) {
                    Method method = new Method();
                    method.returnedType = extractClazzFromType(methodElement.getReturnType());
                    method.isStatic = isAStaticMethod(methodElement);
                    method.name = methodElement.getSimpleName().toString();
                    method.arguments = extractMethodArguments(methodElement);
                    result.add(method);
                }
            }
        }
        return result;
    }

    private List<Argument> extractMethodArguments(ExecutableElement element) {
        List<Argument> result = new ArrayList<>();
        for(VariableElement item : element.getParameters()) {
            Argument argument = new Argument();
            argument.name = item.getSimpleName().toString();
            argument.type = extractClazzFromType(item.asType());
            result.add(argument);
        }
        return result;
    }

    private Clazz extractClazzFromType(TypeMirror type) {
        String packageName = "";
        String className;
        String typeCannonicalName = type.toString();
        int firstMinor = typeCannonicalName.indexOf('<');
        int lastPoint = typeCannonicalName.lastIndexOf('.');
        if (firstMinor > -1) {
            String temporaryExtraction = typeCannonicalName.substring(0, firstMinor);
            packageName = typeCannonicalName.substring(0, temporaryExtraction.lastIndexOf('.'));
            className = typeCannonicalName.substring(packageName.length() + 1);
        } else {
            if (lastPoint > -1) {
                packageName = typeCannonicalName.substring(0, lastPoint);
                className = typeCannonicalName.substring(lastPoint + 1);
            } else {
                className = type.toString();
            }
        }
        return new Clazz(packageName, className);
    }

    private boolean isAConstructor(ExecutableElement methodElement) {
        return methodElement.getSimpleName().toString().equals("<init>");
    }

    private boolean isAPublicMethod(ExecutableElement methodElement) {
        boolean result;
        Set<Modifier> modifiers = methodElement.getModifiers();
        if (modifiers.contains(Modifier.PUBLIC)) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    private boolean isAStaticMethod(ExecutableElement methodElement) {
        boolean result;
        Set<Modifier> modifiers = methodElement.getModifiers();
        if (modifiers.contains(Modifier.STATIC)) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }
}
