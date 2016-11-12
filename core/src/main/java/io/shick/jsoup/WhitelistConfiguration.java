package io.shick.jsoup;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.jsoup.safety.Whitelist;

public interface WhitelistConfiguration {

  void allowedTags(Consumer<String> fn);

  void allowedAttributes(BiConsumer<String, List<String>> fn);

  void enforcedAttributes(BiConsumer<String, Map<String,String>> fn);

  void allowedProtocols(BiConsumer<String, Map<String,List<String>>> fn);

  boolean allowsTag(String tagName);

  boolean hasAllowedAttributes(String tagName);

  boolean allowsAttribute(String tagName, String attrName);

  boolean hasEnforcedAttributes(String tagName);

  boolean enforcesAttribute(String tagName, String attrName);

  boolean enforcesAttribute(String tagName, String attrName, String enforcedValue);

  boolean hasAllowedProtocols(String tagName);

  boolean hasAllowedProtocols(String tagName, String attrName);

  boolean allowsProtocol(String tagName, String attrName, String protocol);
  
  Whitelist apply(Whitelist in);

  Whitelist whitelist();
}
