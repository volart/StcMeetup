package com.genesys.meetup.exception;

@FunctionalInterface
public interface ExceptionHandler {
  /**
   * @param e original exception
   */
  void handle(Throwable e);
}
