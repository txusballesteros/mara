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
package com.txusballesteros.mara.presentation.detail;

import java.util.ArrayList;
import java.util.Collection;
import android.content.Context;
import com.txusballesteros.mara.R;
import com.txusballesteros.mara.TraitBuilder;
import com.txusballesteros.mara.presentation.traits.LoadingTrait;

public class DetailTraitBuilder extends TraitBuilder {
    private Context context;

    public DetailTraitBuilder(Context context) {
        this.context = context;
    }

    @Override
    protected Collection<Object> onPrepareTraits() {
        Collection<Object> result = new ArrayList<>();
        result.add(new LoadingTrait(context));
        return result;
    }
}
