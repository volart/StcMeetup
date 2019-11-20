package com.genesys.meetup.util;

@FunctionalInterface
public interface ActionThrowsAll {
  void execute() throws Exception;
}
