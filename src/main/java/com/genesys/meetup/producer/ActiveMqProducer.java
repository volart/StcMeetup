package com.genesys.meetup.producer;

import com.genesys.meetup.config.BrokerConfig;
import com.google.common.base.Strings;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.ConnectionFactory;

public class ActiveMqProducer extends BaseProducer {

  protected ActiveMqProducer(BrokerConfig config) {
    super(config);
  }

  @Override
  protected ConnectionFactory createJmsConnectionFactory() {
    if (!Strings.isNullOrEmpty(config.getUsername()))
      return new ActiveMQConnectionFactory(config.getUsername(), config.getPassword(), config.getUrl());

    return new ActiveMQConnectionFactory(config.getUrl());
  }

}
