package com.genesys.meetup.message;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class BaseMessage {
  @Getter
  @Setter
  private Long id;
  @Getter
  @Setter
  private String text;

  public BaseMessage() {
  }

  public BaseMessage(Long id, String text) {
    this.id = id;
    this.text = text;
  }
}
