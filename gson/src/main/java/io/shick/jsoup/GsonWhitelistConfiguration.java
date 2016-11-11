package io.shick.jsoup;

import org.jsoup.safety.Whitelist;

import com.google.gson.Gson;

public class GsonWhitelistConfiguration extends BasicWhitelistConfiguration {

  public static Whitelist whitelistFromJson(String json) {
    return fromJson(json).whitelist();
  }

  public static GsonWhitelistConfiguration fromJson(String json) {
    return new Gson().fromJson(json, GsonWhitelistConfiguration.class);
  }

  @Override
  public String format(WhitelistConfiguration config) {
    if (config instanceof BasicWhitelistConfiguration) {
      return new Gson().toJson(config);
    }
    else {
      throw new IllegalArgumentException("I can only format instances of BasicWhitelistConfiguration");
    }
  }
}
  
