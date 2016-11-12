package io.shick.jsoup;

public interface MutableWhitelistConfiguration extends WhitelistConfiguration {
  MutableWhitelistConfiguration allowTag(String tagName);

  MutableWhitelistConfiguration enforceAttribute(String tagName, String attrName, String enforcedValue);

  MutableWhitelistConfiguration allowProtocol(String tagName, String attrName, String protocol);

  MutableWhitelistConfiguration allowAttribute(String tagName, String attrName);
}
