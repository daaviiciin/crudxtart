package com.example.crudxtart.utils;

import java.time.LocalDate;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

public class JsonUtil {

    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class,
                    (JsonSerializer<LocalDate>) (date, type, ctx) ->
                            new JsonPrimitive(date.toString()))
            .registerTypeAdapter(LocalDate.class,
                    (JsonDeserializer<LocalDate>) (json, type, ctx) ->
                            LocalDate.parse(json.getAsString()))
            .registerTypeHierarchyAdapter(HibernateProxy.class,
                    (JsonSerializer<HibernateProxy>) (proxy, type, ctx) -> {
                        // Obtener la entidad real del proxy
                        LazyInitializer initializer = proxy.getHibernateLazyInitializer();
                        if (initializer.isUninitialized()) {
                            // Si el proxy no está inicializado, devolver null o un objeto con solo el ID
                            return new JsonObject();
                        }
                        Object target = initializer.getImplementation();
                        // Serializar la entidad real
                        return ctx.serialize(target);
                    })
            .create();

}
