package io.shick.jsoup;

public interface MutableWhitelistConfiguration extends WhitelistConfiguration {
  void allowTag(String tagName);
  void enforceAttribute(String tagName, String attrName, String enforcedValue);

  void allowProtocol(String tagName, String attrName, String protocol);
  void allowAttribute(String tagName, String attrName);


}
