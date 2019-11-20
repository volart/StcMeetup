package com.genesys.meetup.producer;

import com.genesys.meetup.message.BaseMessage;
import com.genesys.meetup.exception.ExceptionHandler;

public interface Producer {
  void send(BaseMessage baseMessage, ExceptionHandler onError);
  void send(BaseMessage baseMessage, String queueName, ExceptionHandler onError);
}
