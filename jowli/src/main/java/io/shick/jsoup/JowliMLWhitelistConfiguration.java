package io.shick.jsoup;

import static io.shick.jsoup.jowli.parser.JowliMLParser.ROOT;

import org.jsoup.safety.Whitelist;

class JowliMLWhitelistConfiguration extends BasicWhitelistConfiguration {

  public static Whitelist whitelistFromJowliML(String jowliml) {
    return fromJowliML(jowliml).whitelist();
  }

  public static JowliMLWhitelistConfiguration fromJowliML(String json) {
    final JowliMLWhitelistConfiguration c = new JowliMLWhitelistConfiguration();

    ROOT.parse(json).stream()
      .forEach(consumer -> consumer.accept(c));
    return c;
  }


  @Override
  public String format(WhitelistConfiguration config) {
    if (config instanceof BasicWhitelistConfiguration) {
      return "not implemented";
    }
    else {
      throw new IllegalArgumentException("I can only format instances of BasicWhitelistConfiguration");
    }
  }
}
