// Copyright 2020 Google LLC

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

//     https://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.data;

import java.util.ArrayList;

public class Meal {
    private final Long id;
    private final String title;
    private final String description;
    private final ArrayList<String> ingredients;
    private final String type;

    public Meal(Long id, String title, String description, ArrayList<String> ingredients, String type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.ingredients = new ArrayList<String>(ingredients);
        this.type = type;
    }

    public Long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public ArrayList<String> getIngredients() {
        return this.ingredients;
    }

    public String getType() {
        return this.type;
    }
}
