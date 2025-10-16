package com.infisical.sdk.util;

public class Helper {

  public static boolean isNullOrEmpty(String str) {
    return str == null || str.trim().isEmpty();
  }

  public static String booleanToString(Boolean b) {
    return b == null ? null : b.toString();
  }
}
