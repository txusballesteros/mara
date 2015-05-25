Mara
=====================

![Logo](assets/logo.png)

Mara is a library to provide traits or composition capabilities to your Java projects.

But at first place allow me tell who's Mara. Mara is my little princess, she's the first of my two sons and this library is dedicated to she. I love you my princess.

## Why?

Why I do this library, many times I think about how I can do reusable components for my projects and how it can be isolated from all of the rest. A month ago someone show me Kotlin, a language created by JetBrains and based 100% on JVM. And I thought how I can migrate the Traits capability from Kotlin to Java. The answer to the question is this library.

## Version

[ ![Download](https://api.bintray.com/packages/txusballesteros/maven/mara/images/download.svg) ](https://bintray.com/txusballesteros/maven/mara/_latestVersion)

## How to use

Configure the APT Plugin on your build.gradle file.
```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.4'
    }
}

apply plugin: 'com.android.application'
apply plugin: 'com.neenbedankt.android-apt'
```

Add the library dependencies.
```groovy
dependencies {
    ...
    compile 'com.txusballesteros:mara:0.1'
    apt 'com.txusballesteros:mara-compiler:0.1'
}
```

If you are using any other libraries with AnnotationsProcessors like ButterKnife, Realm, etc... You need to set this in your build.gradle to exclude the Processor that is already packaged:
```groovy
packagingOptions {
    exclude 'META-INF/services/javax.annotation.processing.Processor'
}
```

Creating your first Trait.
```java
@Trait
public class MyFirstTrait {
    public void MyMethod() {
        ...
    }
    ...
}
```

Creating your first Composer.
```java
@TraitComposer(
    traits = {
            MyFirstTrait.class,
            ...
    }
)
public interface MyComposer { }
```

Consuming you Composer and your injected Traits.
```java
Mara_MyComposer myComposer =new Mara_MyComposer.Builder().build();
myComposer.MyMethod();
```

## License

Copyright Txus Ballesteros 2015 (@txusballesteros)

This file is part of some open source application.

Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 
Contact: Txus Ballesteros <txus.ballesteros@gmail.com>
