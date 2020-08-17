// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

function searchMeal() {
    fetch('/meal').then(response => response.json()).then((dishes) => 
    {
        const container = document.getElementById("dishes-container");
        container.innerText = "";
        dishes = dishes ?? [""];
        dishes.forEach((dish) => {
            container.appendChild(createListElement(dish));
        });
    });
}

function createListElement(val) {
    const listElement = document.createElement('li');
    listElement.innerText = val;
    return listElement;
}