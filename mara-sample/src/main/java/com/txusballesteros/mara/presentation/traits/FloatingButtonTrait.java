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
package com.txusballesteros.mara.presentation.traits;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.txusballesteros.mara.R;
import com.txusballesteros.mara.Trait;

@Trait
public class FloatingButtonTrait implements Initializable {
    private final Context context;
    private final int rootViewResourceId;
    private OnFlaotingButtonClickListener clickListener;

    public interface OnFlaotingButtonClickListener {
        void onFloatingButtonClick();
    }

    public void setOnFlaotingButtonClickListener(OnFlaotingButtonClickListener listener) {
        this.clickListener = listener;
    }

    public FloatingButtonTrait(Context context, int rootViewResourceId) {
        this.context = context;
        this.rootViewResourceId = rootViewResourceId;
    }

    @Override
    public void initialize() {
        ViewGroup rootView = (ViewGroup)((Activity)context).findViewById(rootViewResourceId);
        View floatingButtonView = LayoutInflater.from(context).inflate(R.layout.trait_floating_button, rootView, false);
        rootView.addView(floatingButtonView, 0);
        floatingButtonView.findViewById(R.id.floatingButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onFloatingButtonClick();
                }
            }
        });
    }
}
