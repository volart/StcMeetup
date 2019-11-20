package com.genesys.meetup.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BrokerConfig {

  public enum BrokerType {
    ACTIVE_MQ,
    SQS
  }

  @Getter @Setter private String url;
  @Getter @Setter private String username;
  @Getter @Setter private String password;
  @Getter @Setter private String region;
  @Getter @Setter private String queueName;
  @Getter @Setter private BrokerType type;


}
