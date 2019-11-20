package com.genesys.meetup.consumer;

import com.genesys.meetup.config.BrokerConfig;
import com.genesys.meetup.exception.ConsumerException;
import com.genesys.meetup.util.Actions;
import lombok.extern.slf4j.Slf4j;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

@Slf4j
public abstract class BaseConsumer implements Consumer {

  private static final boolean JMS_SESSION_TRANSACTED = false;

  protected Connection jmsConnection;
  protected Session jmsSession;

  protected final BrokerConfig config;

  private MessageConsumer jmsConsumer;
  private MessageHandler messageHandler;

  protected BaseConsumer(BrokerConfig config) {
    this.config = config;
  }

  @Override
  public synchronized void start() {

    ConnectionFactory jmsConnectionFactory = createJmsConnectionFactory();

    try {
      jmsConnection = jmsConnectionFactory.createConnection();
      log.debug("created JMS connection {}", jmsConnection);
    } catch (JMSException e) {
      throw new ConsumerException("failed to create connection ", e);
    }

    try {
      jmsSession = jmsConnection.createSession(JMS_SESSION_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
      log.debug("created JMS session {}", jmsSession);
    } catch (JMSException e) {
      throw new ConsumerException("failed to create session", e);
    }

    Destination destination;
    String queueName = config.getQueueName();
    try {
      destination = createDestination(queueName);
      log.debug("created destination {}", queueName);
    } catch (JMSException e) {
      throw new ConsumerException(String.format("failed to create destination %s for session %s", queueName, jmsSession), e);
    }

    try {
      jmsConsumer = jmsSession.createConsumer(destination);
    } catch (JMSException e) {
      throw new ConsumerException(String.format("failed to create consumer on destination %s for session %s", queueName, jmsSession), e);
    }

    try {
      jmsConsumer.setMessageListener(messageHandler::onMessage);
    } catch (JMSException e) {
      throw new ConsumerException(String.format("failed to set message listener on destination %s for session %s", queueName, jmsSession), e);
    }

    try {
      jmsConnection.start();
      log.debug("started JMS jmsConnection {}", jmsConnection);
    } catch (JMSException e) {
      throw new ConsumerException("failed to start JMS jmsConnection " + jmsConnection, e);
    }

    log.debug("started consumer on session {}", jmsSession);
  }

  @Override
  public synchronized void stop() {
    Actions.silent(jmsConsumer::close);
    Actions.silent(jmsSession::close);
    Actions.silent(jmsConnection::close);
  }

  @Override
  public void setMessageHandler(MessageHandler messageHandler) {
    this.messageHandler = messageHandler;
  }

  protected Destination createDestination(String destinationName) throws JMSException {
    return jmsSession.createQueue(destinationName);
  }

  protected abstract ConnectionFactory createJmsConnectionFactory();
}
