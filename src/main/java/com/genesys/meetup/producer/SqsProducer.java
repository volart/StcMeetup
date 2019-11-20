package com.genesys.meetup.producer;

import com.genesys.meetup.config.BrokerConfig;
import com.genesys.meetup.util.SqsHelper;

import javax.jms.ConnectionFactory;

public class SqsProducer extends BaseProducer {

  protected SqsProducer(BrokerConfig config) {
    super(config);
  }

  @Override
  protected ConnectionFactory createJmsConnectionFactory() {
    return SqsHelper.createJmsConnectionFactory(config.getUrl(), config.getRegion());
  }

}
