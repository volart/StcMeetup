package com.genesys.meetup.consumer;

import javax.jms.Message;

@FunctionalInterface
public interface MessageHandler {
  void onMessage(Message jmsMessage);
}
