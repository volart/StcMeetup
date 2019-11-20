package com.genesys.meetup.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Actions {
  private Actions() {
  }

  public static void silent(ActionThrowsAll action) {
    try {
      if (action == null)
        return;

      action.execute();
    } catch (Exception ex) {
      log.warn("failed to execute action " + action.getClass().getSimpleName(), ex);
    }
  }
}
