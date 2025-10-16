package com.infisical.sdk.util;

import java.util.Random;

public class RandomUtil {
  private static String candidateChars =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmopqrstuvwxyz";

  public static String generateRandomString(Integer length) {
    StringBuilder salt = new StringBuilder();
    Random rnd = new Random();
    while (salt.length() < length) {
      int index = (int) (rnd.nextFloat() * candidateChars.length());
      salt.append(candidateChars.charAt(index));
    }

    return salt.toString();
  }
}
