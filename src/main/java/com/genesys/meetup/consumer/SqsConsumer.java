package com.genesys.meetup.consumer;

import com.genesys.meetup.config.BrokerConfig;
import com.genesys.meetup.util.SqsHelper;
import lombok.extern.slf4j.Slf4j;

import javax.jms.ConnectionFactory;

@Slf4j
public class SqsConsumer extends BaseConsumer {

  protected SqsConsumer(BrokerConfig config) {
    super(config);
  }

  @Override
  protected ConnectionFactory createJmsConnectionFactory() {
    return SqsHelper.createJmsConnectionFactory(config.getUrl(), config.getRegion());
  }

}
