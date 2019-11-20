package com.genesys.meetup.exception;

import javax.jms.JMSException;

public class ConsumerException extends RuntimeException {
  public ConsumerException(String msg, JMSException e) {
    super(msg, e);
  }
}
