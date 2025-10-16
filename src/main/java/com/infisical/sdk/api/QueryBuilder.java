package com.infisical.sdk.api;

import java.util.HashMap;
import java.util.Map;

public class QueryBuilder {
  private final Map<String, String> params = new HashMap<>();

  public QueryBuilder add(String key, Object value) {
    if (value != null) {
      params.put(key, value.toString());
    }
    return this;
  }

  public Map<String, String> build() {
    return params;
  }
}
