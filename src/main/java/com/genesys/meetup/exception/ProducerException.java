package com.genesys.meetup.exception;

import com.fasterxml.jackson.core.JsonProcessingException;


public class ProducerException extends RuntimeException {
  public ProducerException(String s, JsonProcessingException e) {
    super(s, e);
  }
}
