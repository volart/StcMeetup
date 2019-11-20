package com.genesys.meetup.producer;

import com.genesys.meetup.config.BrokerConfig;

public class ProducerFactory {

  public Producer createProducer(BrokerConfig brokerConfig) {
    if (brokerConfig.getType() == BrokerConfig.BrokerType.ACTIVE_MQ) {
      return new ActiveMqProducer(brokerConfig);
    }
    return new SqsProducer(brokerConfig);
  }
}
