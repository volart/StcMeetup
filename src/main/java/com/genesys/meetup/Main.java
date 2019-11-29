package com.genesys.meetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genesys.meetup.config.BrokerConfig;
import com.genesys.meetup.consumer.Consumer;
import com.genesys.meetup.consumer.ConsumerFactory;
import com.genesys.meetup.message.BaseMessage;
import com.genesys.meetup.producer.Producer;
import com.genesys.meetup.producer.ProducerFactory;
import com.genesys.meetup.util.SqsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.IOException;

import static com.genesys.meetup.config.BrokerConfig.BrokerType.ACTIVE_MQ;
import static com.genesys.meetup.config.BrokerConfig.BrokerType.SQS;

@Slf4j
public class Main {

  private static final int ALPABET_COUNT = 10;
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


  private static final ConsumerFactory CONSUMER_FACTORY = new ConsumerFactory();
  private static final ProducerFactory PRODUCER_FACTORY = new ProducerFactory();

  public static void main(String[] args) {

    BrokerConfig brokerConfig;
    brokerConfig = getActiveMqConfig();
//    brokerConfig = getSqsConfig();

    Consumer consumer = CONSUMER_FACTORY.createConsumer(brokerConfig);

    consumer.setMessageHandler(m -> {
      if (m instanceof TextMessage) {
        try {
          String json = ((TextMessage) m).getText();
          BaseMessage baseMessage = OBJECT_MAPPER.readValue(json, BaseMessage.class);
          log.info("Message has been received successfully {}", baseMessage);
        } catch (JMSException | IOException e) {
          log.error("Can't process message {}", m, e);
        }
      }
    });

    consumer.start();

    Producer producer = PRODUCER_FACTORY.createProducer(brokerConfig);

    for (long i = 0; i < 10; i++) {
      BaseMessage baseMessage = getBaseMessage(i);
      producer.send(baseMessage, e -> {
        log.error("Can't send message {}", baseMessage, e);
      });
    }

    consumer.stop();
  }


  private static BrokerConfig getActiveMqConfig() {
    return BrokerConfig.builder().type(ACTIVE_MQ).url("tcp://localhost:61617").username("admin").password("admin").queueName("meetup").build();
  }

  private static BrokerConfig getSqsConfig() {
    String url = "http://localhost:9324";
    String region = "eu-central-1";
    String queueName = "meetup";

    SqsHelper.createSqsQueue(url, region, queueName);

    return BrokerConfig.builder().type(SQS).url(url).region(region).queueName(queueName).build();
  }


  private static BaseMessage getBaseMessage(Long id) {
    return new BaseMessage(id, RandomStringUtils.randomAlphabetic(ALPABET_COUNT));
  }

}
