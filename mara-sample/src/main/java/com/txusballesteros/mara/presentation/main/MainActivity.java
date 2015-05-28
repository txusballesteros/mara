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
package com.txusballesteros.mara.presentation.main;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.txusballesteros.mara.R;
import com.txusballesteros.mara.presentation.BaseActivity;
import com.txusballesteros.mara.presentation.traits.FloatingButtonTrait;

import java.util.Collection;

public class MainActivity extends BaseActivity implements MainPresenter.View {
    private Mara_MainActivityComposer composer;
    private MainPresenter presenter;
    private RecyclerView list;
    private MainListAdapter adapter;

    public MainActivity() {
        presenter = new MainPresenter(this);
        composer = new Mara_MainActivityComposer.Builder()
                                .setContext(this)
                                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureTraitsComposer();
        configureList();
        presenter.onViewCreated();
    }

    private void configureList() {
        adapter = new MainListAdapter();
        list = (RecyclerView)findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
    }

    private void configureTraitsComposer() {
        composer.setOnFlaotingButtonClickListener(new FloatingButtonTrait.OnFlaotingButtonClickListener() {
            @Override
            public void onFloatingButtonClick() {
                presenter.run();
            }
        });
        composer.initialize();
    }

    @Override
    public void renderList(Collection<String> data) {
        adapter.addAll(data);
    }

    @Override
    public void showLoading() {
        composer.showLoading();
        composer.hideFloatingButton();
    }

    @Override
    public void hideLoading() {
        composer.hideLoading();
        composer.showFloatingButton();
    }
}
