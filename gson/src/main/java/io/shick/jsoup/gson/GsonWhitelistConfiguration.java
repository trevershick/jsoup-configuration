package io.shick.jsoup.gson;

import io.shick.jsoup.BasicWhitelistConfiguration;

public class GsonWhitelistConfiguration extends BasicWhitelistConfiguration {

  protected GsonWhitelistConfiguration() {}

  public String toString() {
    return new GsonFormatter().format(this).toString();
  }
  
}
  
