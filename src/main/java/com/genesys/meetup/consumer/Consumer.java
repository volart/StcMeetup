package com.genesys.meetup.consumer;

public interface Consumer {
  void start();
  void stop();
  void setMessageHandler(MessageHandler messageHandler);
}
