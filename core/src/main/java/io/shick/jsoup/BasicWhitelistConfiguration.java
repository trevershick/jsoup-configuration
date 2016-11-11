package io.shick.jsoup;

import static io.shick.jsoup.Func.hashMap;
import static io.shick.jsoup.Func.list;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.safety.Whitelist;

abstract class BasicWhitelistConfiguration implements MutableWhitelistConfiguration {

  private List<String> tags = new ArrayList<>();

  private Map<String, List<String>> attributes = new HashMap<>();

  private Map<String, Map<String, String>> enforcedAttributes = new HashMap<>();

  private Map<String, Map<String, List<String>>> protocols = new HashMap<>();

  public void allowTag(String tagName) {
    tags.add(tagName);
  }

  public boolean allowsTag(String tagName) {
    requireTagName(tagName);
    return tags != null
      && tags.contains(tagName);
  }

  public boolean hasAllowedAttributes(String tagName) {
    requireTagName(tagName);
    return attributes != null
      && attributes.containsKey(tagName);
  }

  public void allowAttribute(String tagName, String attrName) {
    requireTagName(tagName);
    requireAttrName(attrName);
    attributes.merge(tagName, list(attrName), Func::conj);
  }



  public boolean allowsAttribute(String tagName, String attrName) {
    requireAttrName(attrName);
    return hasAllowedAttributes(tagName)
      && attributes.get(tagName) != null
      && attributes.get(tagName).contains(attrName);
  }

  public boolean hasEnforcedAttributes(String tagName) {
    requireTagName(tagName);
    return enforcedAttributes != null
      && enforcedAttributes.containsKey(tagName);
  }

  public boolean enforcesAttribute(String tagName, String attrName) {
    requireAttrName(attrName);
    return hasEnforcedAttributes(tagName)
      && enforcedAttributes.get(tagName) != null
      && enforcedAttributes.get(tagName).get(attrName) != null;
  }

  public boolean enforcesAttribute(String tagName, String attrName, String enforcedValue) {
    requireEnforcedValue(enforcedValue);
    return enforcesAttribute(tagName, attrName)
      && enforcedAttributes.get(tagName).get(attrName).equals(enforcedValue);
  }

  public void enforceAttribute(String tagName, String attrName, String enforcedValue) {
    requireTagName(tagName);
    requireAttrName(attrName);
    requireEnforcedValue(enforcedValue);
    enforcedAttributes.merge(tagName, hashMap(attrName, enforcedValue), Func::merge1);
  }

  public boolean hasAllowedProtocols(String tagName) {
    requireTagName(tagName);
    return protocols != null
      && protocols.containsKey(tagName);
  }

  public boolean hasAllowedProtocols(String tagName, String attrName) {
    requireAttrName(attrName);
    return hasAllowedProtocols(tagName)
      && protocols.get(tagName) != null
      && protocols.get(tagName).containsKey(attrName);
  }

  public void allowProtocol(String tagName, String attrName, String protocol) {
    requireTagName(tagName);
    requireAttrName(attrName);
    requireProtocol(protocol);

    protocols.merge(tagName, hashMap(attrName, list(protocol)), Func::merge2);
  }

  public boolean allowsProtocol(String tagName, String attrName, String protocol) {
    requireProtocol(protocol);
    return hasAllowedProtocols(tagName, attrName)
      && protocols.get(tagName).get(attrName).contains(protocol);
  }

  private void requireTagName(String tagName) {
    requireNonNull(tagName, "tagName cannot be null");
  }

  private void requireAttrName(String attrName) {
    requireNonNull(attrName, "attrName cannot be null");
  }

  private void requireEnforcedValue(String enforcedValueCannot) {
    requireNonNull(enforcedValueCannot, "enforcedValueCannot be null");
  }

  private void requireProtocol(String protocol) {
    requireNonNull(protocol, "protocol cannot be null");
  }

  @Override
  public Whitelist apply(Whitelist in) {
    applyTags(in);
    applyAttributes(in);
    applyEnforcedAttributes(in);
    applyProtocols(in);
    return in;
  }

  @Override
  public Whitelist whitelist() {
    return apply(Whitelist.none());
  }

  private void applyTags(Whitelist in) {
    if (tags == null) {
      return;
    }
    in.addTags(tags.toArray(new String[tags.size()]));
  }

  private void applyAttributes(Whitelist in) {
    if (attributes == null) {
      return;
    }
    for (Map.Entry<String, List<String>> attributeEntry : attributes.entrySet()) {
      in.addAttributes(attributeEntry.getKey(),
        attributeEntry.getValue().toArray(new String[attributeEntry.getValue().size()]));
    }
  }

  private void applyEnforcedAttributes(Whitelist in) {
    if (enforcedAttributes == null) {
      return;
    }
    for (Map.Entry<String, Map<String, String>> enforcedTag : enforcedAttributes.entrySet()) {
      final String tag = enforcedTag.getKey();
      for (Map.Entry<String, String> enforcedAttribute : enforcedTag.getValue().entrySet()) {
        final String attribute = enforcedAttribute.getKey();
        final String value = enforcedAttribute.getValue();
        // if we've set a rule for the protocol, then let's ensure we can add the attribute to the tag
        in.addAttributes(tag, attribute);
        // add the enforced attributes
        in.addEnforcedAttribute(tag, attribute, value);
      }
    }
  }

  private void applyProtocols(Whitelist in) {
    if (protocols == null) {
      return;
    }
    for (Map.Entry<String, Map<String, List<String>>> enforcedTag : protocols.entrySet()) {
      final String tag = enforcedTag.getKey();
      for (Map.Entry<String, List<String>> enforcedAttribute : enforcedTag.getValue().entrySet()) {
        final String attribute = enforcedAttribute.getKey();
        final List<String> value = enforcedAttribute.getValue();
        // if we've set a rule for the protocol, then let's ensure we can add the attribute to the tag
        in.addAttributes(tag, attribute);
        // add the allowed protocols
        in.addProtocols(tag, attribute, value.toArray(new String[value.size()]));
      }
    }
  }
}
