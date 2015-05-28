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

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.txusballesteros.mara.R;
import com.txusballesteros.mara.presentation.navigation.Navigator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.ViewHolder> {
    private List<String> dataset;

    public MainListAdapter() {
        this.dataset = new ArrayList<>();
    }

    public void clear() {
        dataset.clear();
        notifyDataSetChanged();
    }

    public void addAll(Collection<String> data) {
        dataset.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = (TextView)LayoutInflater.from(parent.getContext())
                                                        .inflate(R.layout.item_main, parent, false);
        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        String currentValue = dataset.get(position);
        viewHolder.renderText(currentValue);
    }

    @Override
    public int getItemCount() { return dataset.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView view;

        public void renderText(String value) {
            view.setText(value);
        }

        public ViewHolder(TextView view) {
            super(view);
            this.view = view;
            this.view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
           Navigator.navigateToDetail(view.getContext());
        }
    }
}
