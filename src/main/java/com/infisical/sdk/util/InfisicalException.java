package com.infisical.sdk.util;

import java.io.IOException;
import lombok.Getter;

public class InfisicalException extends Exception {
  @Getter private final String message;
  private final StackTraceElement[] stackTrace;

  public InfisicalException(String message) {
    super(message); // Pass message to the parent Exception class
    this.message = message;
    this.stackTrace = null;
  }

  // Constructor that takes the original ApiException
  public InfisicalException(IOException cause) {
    this.stackTrace = cause.getStackTrace();

    if (cause.getMessage() != null) {
      this.message = cause.getMessage();
    } else {
      this.message = null;
    }
  }
}
