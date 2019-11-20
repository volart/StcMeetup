package com.genesys.meetup.consumer;

import com.genesys.meetup.config.BrokerConfig;

public class ConsumerFactory {

  public Consumer createConsumer(BrokerConfig brokerConfig){
    if(brokerConfig.getType() == BrokerConfig.BrokerType.ACTIVE_MQ) {
      return new ActiveMqConsumer(brokerConfig);
    }
    return new SqsConsumer(brokerConfig);
  }
}
