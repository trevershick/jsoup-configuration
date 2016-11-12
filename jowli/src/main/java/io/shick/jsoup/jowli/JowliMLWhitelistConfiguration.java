package io.shick.jsoup.jowli;

import io.shick.jsoup.BasicWhitelistConfiguration;

import java.text.ParseException;

public class JowliMLWhitelistConfiguration extends BasicWhitelistConfiguration {

  public JowliMLWhitelistConfiguration() {}
  
  public String toString() {
    return new JowliMLFormatter().format(this).toString();
  }
}
