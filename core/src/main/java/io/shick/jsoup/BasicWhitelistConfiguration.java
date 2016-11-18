package io.shick.jsoup;

import static io.shick.jsoup.util.Func.hashMap;
import static io.shick.jsoup.util.Func.list;
import static java.util.Objects.requireNonNull;

import io.shick.jsoup.util.Func;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jsoup.safety.Whitelist;

/**
 * <p>BasicWhitelistConfiguration class.</p>
 *
 * @author Trever Shick - trever@shick.io
 */
public class BasicWhitelistConfiguration implements MutableWhitelistConfiguration {

  private String base = null;
  
  private List<String> tags = null;

  private Map<String, List<String>> attributes = null;

  private Map<String, Map<String, String>> enforcedAttributes = null;

  private Map<String, Map<String, List<String>>> protocols = null;

  private List<String> tags() {
    return Optional.ofNullable(tags).orElseGet(Collections::emptyList);
  }

  private Map<String, List<String>> attributes() {
    return Optional.ofNullable(attributes).orElseGet(Collections::emptyMap);
  }

  private Map<String, Map<String, String>> enforcedAttributes() {
    return Optional.ofNullable(enforcedAttributes).orElseGet(Collections::emptyMap);
  }

  private Map<String, Map<String, List<String>>> protocols() {
    return Optional.ofNullable(protocols).orElseGet(Collections::emptyMap);
  }

  /**
   * {@inheritDoc}
   * @return
   */
  @Override
  public String base() {
    return base;
  }

  /**
   * {@inheritDoc}
   */
  public MutableWhitelistConfiguration allowTag(String tagName) {
    if (tags == null) {
      tags = new ArrayList();
    }
    tags().add(tagName);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public void allowedTags(Consumer<String> fn) {
    tags().forEach(fn);
  }

  /**
   * {@inheritDoc}
   */
  public void allowedAttributes(BiConsumer<String, List<String>> fn) {
    this.attributes().forEach(fn);
  }

  /**
   * {@inheritDoc}
   */
  public void enforcedAttributes(BiConsumer<String, Map<String, String>> fn) {
    enforcedAttributes().forEach(fn);
  }

  /**
   * {@inheritDoc}
   */
  public void allowedProtocols(BiConsumer<String, Map<String, List<String>>> fn) {
    protocols().forEach(fn);
  }

  /**
   * {@inheritDoc}
   */
  public boolean allowsTag(String tagName) {
    requireTagName(tagName);
    return tags().contains(tagName);
  }

  /**
   * {@inheritDoc}
   */
  public boolean hasAllowedAttributes(String tagName) {
    requireTagName(tagName);
    return attributes().containsKey(tagName);
  }

  /**
   * {@inheritDoc}
   */
  public MutableWhitelistConfiguration allowAttribute(String tagName, String attrName) {
    requireTagName(tagName);
    requireAttrName(attrName);
    if (attributes == null) {
      attributes = new HashMap();
    }
    attributes().merge(tagName, list(attrName), Func::concat);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public boolean allowsAttribute(String tagName, String attrName) {
    requireAttrName(attrName);
    return hasAllowedAttributes(tagName)
      && attributes().get(tagName).contains(attrName);
  }

  /**
   * {@inheritDoc}
   */
  public boolean hasEnforcedAttributes(String tagName) {
    requireTagName(tagName);
    return enforcedAttributes().containsKey(tagName);
  }

  /**
   * {@inheritDoc}
   */
  public boolean enforcesAttribute(String tagName, String attrName) {
    requireAttrName(attrName);
    return hasEnforcedAttributes(tagName)
      && enforcedAttributes().get(tagName).get(attrName) != null;
  }

  /**
   * {@inheritDoc}
   */
  public boolean enforcesAttribute(String tagName, String attrName, String enforcedValue) {
    requireEnforcedValue(enforcedValue);
    return enforcesAttribute(tagName, attrName)
      && enforcedAttributes().get(tagName).get(attrName).equals(enforcedValue);
  }

  /**
   * {@inheritDoc}
   */
  public MutableWhitelistConfiguration enforceAttribute(String tagName, String attrName, String enforcedValue) {
    requireTagName(tagName);
    requireAttrName(attrName);
    requireEnforcedValue(enforcedValue);
    if (enforcedAttributes == null) {
      enforcedAttributes = new HashMap();
    }
    enforcedAttributes().merge(tagName, hashMap(attrName, enforcedValue), Func::merge1);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public boolean hasAllowedProtocols(String tagName) {
    requireTagName(tagName);
    return protocols().containsKey(tagName);
  }

  /**
   * {@inheritDoc}
   */
  public boolean hasAllowedProtocols(String tagName, String attrName) {
    requireAttrName(attrName);
    return hasAllowedProtocols(tagName)
      && protocols().get(tagName).containsKey(attrName);
  }

  /**
   * {@inheritDoc}
   */
  public MutableWhitelistConfiguration allowProtocol(String tagName, String attrName, String protocol) {
    requireTagName(tagName);
    requireAttrName(attrName);
    requireProtocol(protocol);
    if (protocols == null) {
      protocols = new HashMap();
    }

    protocols().merge(tagName, hashMap(attrName, list(protocol)), Func::merge2);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public boolean allowsProtocol(String tagName, String attrName, String protocol) {
    requireProtocol(protocol);
    return hasAllowedProtocols(tagName, attrName)
      && protocols().get(tagName).get(attrName).contains(protocol);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MutableWhitelistConfiguration base(String base) {
    this.base = base;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Whitelist apply(Whitelist whitelist) {
    applyTags(whitelist);
    applyAttributes(whitelist);
    applyEnforcedAttributes(whitelist);
    applyProtocols(whitelist);
    return whitelist;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Whitelist whitelist() {
    // use the supplier, but if there isn't one then use 'Whitelist.none()'
    final String b = Optional.ofNullable(this.base)
      .orElse(BaseFactories.NONE);

    final Supplier<Whitelist> whitelistSupplier = Optional.ofNullable(b)
      .map(BaseFactories.FACTORIES::get)
      .orElseThrow(() -> new IllegalStateException("Invalid base: " + b));

    return apply(whitelistSupplier.get());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BasicWhitelistConfiguration)) {
      return false;
    }
    BasicWhitelistConfiguration that = (BasicWhitelistConfiguration) o;
    return Objects.equals(tags(), that.tags()) &&
      Objects.equals(attributes(), that.attributes()) &&
      Objects.equals(enforcedAttributes(), that.enforcedAttributes()) &&
      Objects.equals(protocols(), that.protocols());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(tags(), attributes(), enforcedAttributes(), protocols());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("BasicWhitelistConfiguration{");
    sb.append("tags=").append(tags);
    sb.append(", attributes=").append(attributes);
    sb.append(", enforcedAttributes=").append(enforcedAttributes);
    sb.append(", protocols=").append(protocols);
    sb.append('}');
    return sb.toString();
  }

  private void applyTags(Whitelist in) {
    in.addTags(tags().toArray(new String[tags().size()]));
  }

  private void applyAttributes(Whitelist in) {
    for (Map.Entry<String, List<String>> attributeEntry : attributes().entrySet()) {
      in.addAttributes(attributeEntry.getKey(),
        attributeEntry.getValue().toArray(new String[attributeEntry.getValue().size()]));
    }
  }

  private void applyEnforcedAttributes(Whitelist in) {
    for (Map.Entry<String, Map<String, String>> enforcedTag : enforcedAttributes().entrySet()) {
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
    for (Map.Entry<String, Map<String, List<String>>> enforcedTag : protocols().entrySet()) {
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
}
