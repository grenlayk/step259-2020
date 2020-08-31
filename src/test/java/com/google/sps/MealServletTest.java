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

package com.google.sps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.google.sps.data.Meal;
import com.google.sps.servlets.MealServlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class MealServletTest{
    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private static final Meal MEAL_EMPTY = new Meal(
        0L, "", "", new ArrayList<>(), "");
    private static final Meal MEAL_1 = new Meal(
        1L, "Fried potato", "Fried potato with mushrooms and onion.",
            new ArrayList<>(Arrays.asList("potato", "onion", "oil")), "Main");
    private static final Meal MEAL_1_DUPLICATE = new Meal(
        1L, "Vegetable soup", "Vegetable soup with onion.",
            new ArrayList<>(Arrays.asList("potato", "onion")), "Soup");
    private static final Meal MEAL_2 = new Meal(
        2L, "Chocolate cake", "Chocolate cake with butter cream and strawberry.",
            new ArrayList<>(Arrays.asList("flour", "water", "butter", "strawberry")), "Dessert");
    


    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    // TODO(sandatsian): add more specific tests for search feature and get list feature.
    // TODO(grenlayk): add test for get similar feature.
    // Get one of the existing objects Meal by id.
    @Test
    public void getMealByIdTest() throws IOException, ServletException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(createMealEntity(MEAL_1));
        ds.put(createMealEntity(MEAL_2));
        
        MealServlet servlet = new MealServlet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setPathInfo("/2");
        servlet.doGet(request, response);
        Gson gson = new Gson();
        String expected = gson.toJson(MEAL_2);
        String actual = response.getContentAsString().trim();

        assertEquals(expected, actual);
    }

    // Get an object from empty datastore.
    // Expected result: Response Status NOT FOUND.
    @Test
    public void getMealByIdFromEmptyDsTest() throws IOException, ServletException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        
        MealServlet servlet = new MealServlet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setPathInfo("/2");
        servlet.doGet(request, response);
        int expected = HttpServletResponse.SC_NOT_FOUND;
        int actual = response.getStatus();

        assertEquals(expected, actual);
    }

    // From datastore, where two entities with same id exist, get an object by this id.
    // Expected result: Response Status INTERNAL SERVER ERROR.
    @Test
    public void getMealByIdForMultipleEntitiesTest() throws IOException, ServletException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(createMealEntity(MEAL_1));
        ds.put(createMealEntity(MEAL_1_DUPLICATE));
        
        MealServlet servlet = new MealServlet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setPathInfo("/1");
        servlet.doGet(request, response);
        int expected = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        int actual = response.getStatus();

        assertEquals(expected, actual);
    }

    // Get a Meal object from datastore, that can't be created (invalid field value).
    // Expected result: Response Status SC_NOT_FOUND.
    @Test
    public void getEmptyMealByIdTest() throws IOException, ServletException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(createMealEntity(MEAL_EMPTY));
        ds.put(createMealEntity(MEAL_1));

        MealServlet servlet = new MealServlet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setPathInfo("/0");
        servlet.doGet(request, response);
        int expected = HttpServletResponse.SC_NOT_FOUND;
        int actual = response.getStatus();

        assertEquals(expected, actual);
    }

    // Get a full list of objects Meal from datastore.
    // Expected result: a JSON String list with two Meal objects.
    @Test
    public void getMealListTest() throws IOException, ServletException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(createMealEntity(MEAL_1));
        ds.put(createMealEntity(MEAL_2));
        
        MealServlet servlet = new MealServlet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doGet(request, response);
        List<Meal> meals = new ArrayList<>();
        meals.add(MEAL_1);
        meals.add(MEAL_2);
        
        Gson gson = new Gson();
        String expected = gson.toJson(meals);
        String actual = response.getContentAsString().trim();
        assertEquals(expected, actual);
    }

    // Request with invalid pathInfo ""meal/1/"
    // Expected result: Response Status BAD REQUEST
    @Test
    public void getInvalidPathInfoTest() throws IOException, ServletException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(createMealEntity(MEAL_1));

        MealServlet servlet = new MealServlet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setPathInfo("/1/");
        servlet.doGet(request, response);
        int expected = HttpServletResponse.SC_BAD_REQUEST;
        int actual = response.getStatus();

        assertEquals(expected, actual);
    }

    /**
     * Creates an entity for Datastore with properties of class Meal.
     * @param meal object of class Meal for which the entity is creating.
     * @return new Entity object with necessary properties.
     */
    private Entity createMealEntity(Meal meal) {
        Entity mealEntity = new Entity("Meal");
        mealEntity.setProperty("id", meal.getId());
        mealEntity.setProperty("title", meal.getTitle());
        mealEntity.setProperty("description", meal.getDescription());
        mealEntity.setProperty("ingredients", meal.getIngredients());
        mealEntity.setProperty("type", meal.getType());
 
        return mealEntity;
    }   
}