package com.genesys.meetup.producer;

import com.amazon.sqs.javamessaging.SQSConnection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genesys.meetup.config.BrokerConfig;
import com.genesys.meetup.exception.ExceptionHandler;
import com.genesys.meetup.exception.ProducerException;
import com.genesys.meetup.message.BaseMessage;
import com.genesys.meetup.util.Actions;
import lombok.extern.slf4j.Slf4j;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

@Slf4j
public abstract class BaseProducer implements Producer {

  public static final ObjectMapper JSON_MAPPER = new ObjectMapper();

  protected final BrokerConfig config;
  protected final ConnectionFactory connectionFactory;

  protected BaseProducer(BrokerConfig config) {
    this.config = config;
    this.connectionFactory = createJmsConnectionFactory();
  }

  @Override
  public void send(BaseMessage baseMessage, ExceptionHandler onError) {
    send(baseMessage, config.getQueueName(), onError);
  }

  @Override
  public void send(BaseMessage baseMessage, String queueName, ExceptionHandler onError) {
    try {
      final Connection connection = getConnection();

      try {
        final Session session = getSession(connection);

        try {
          TextMessage jmsMessage = createJmsMessage(session, baseMessage);

          // Look at Sending Messages Synchronously https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/getting-started.html
          if (connection instanceof SQSConnection)
            jmsMessage.setStringProperty("JMSXGroupID", "Default");

          Destination queue = getDestination(queueName, session);
          MessageProducer producer = getProducer(session, queue);

          sendToBroker(jmsMessage, producer, queue);
        } finally {
          Actions.silent(session::close);
        }
      } finally {
        Actions.silent(connection::close);
      }
    } catch (Exception e) {
      log.warn("Message can't be sent to broker", e);
      onError.handle(e);
    }
  }

  protected Session getSession(Connection connection) throws JMSException {
    return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
  }

  protected MessageProducer getProducer(Session session, Destination queue) throws JMSException {
    return session.createProducer(queue);
  }

  protected void sendToBroker(TextMessage jmsMessage, MessageProducer producer, Destination queue) throws JMSException {
    try {
      producer.send(jmsMessage);
      log.info("Message {} has been successfully sent to broker queue {}", jmsMessage.getJMSMessageID(), queue);
    } finally {
      Actions.silent(producer::close);
    }
  }

  protected TextMessage createJmsMessage(Session session, BaseMessage baseMessage) throws JMSException {
    return session.createTextMessage(toJson(baseMessage));
  }

  protected Connection getConnection() throws JMSException {
    return connectionFactory.createConnection();
  }

  protected Destination getDestination(String queueName, Session session) throws JMSException {
    return session.createQueue(queueName);
  }

  protected abstract ConnectionFactory createJmsConnectionFactory();

  private String toJson(BaseMessage message) {
    try {
      return JSON_MAPPER.writeValueAsString(message);
    } catch (JsonProcessingException e) {
      throw new ProducerException("failed to serialize a message " + message.toString(), e);
    }
  }

}
