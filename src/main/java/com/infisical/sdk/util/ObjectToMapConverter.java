package com.infisical.sdk.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.annotations.SerializedName;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ObjectToMapConverter {
  private static final ObjectMapper objectMapper;

  static {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  public static Map<String, String> toStringMap(Object obj) {
    Map<String, String> result = new HashMap<>();
    Class<?> clazz = obj.getClass();

    while (clazz != null) {
      for (Field field : clazz.getDeclaredFields()) {
        field.setAccessible(true);
        try {
          Object value = field.get(obj);

          // Use @SerializedName if present, otherwise use field name
          String key = field.getName();
          SerializedName serializedName = field.getAnnotation(SerializedName.class);
          if (serializedName != null) {
            key = serializedName.value();
          }

          result.put(key, value != null ? value.toString() : null);
        } catch (IllegalAccessException e) {
          // Skip inaccessible fields
        }
      }
      clazz = clazz.getSuperclass();
    }

    return result;
  }
}
