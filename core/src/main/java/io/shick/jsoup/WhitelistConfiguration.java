package io.shick.jsoup;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.jsoup.safety.Whitelist;

/**
 * <p>WhitelistConfiguration interface.</p>
 *
 * @author Trever Shick - trever@shick.io
 */
public interface WhitelistConfiguration {

  /**
   * @return the name of the 'base' whitelist used when {@link #whitelist()} is called.
   */
  String base();

  /**
   * <p><em>fn</em> will be called once for each allowed tag.</p>
   *
   * @param fn (non null) a {@link java.util.function.Consumer} object.
   */
  void allowedTags(Consumer<String> fn);

  /**
   * <p>
   * <em>fn</em> will be called once for each allowed tag
   * with the allowed attributes as the second argument, for
   * example <em>accept('a',['href','rel'])</em>
   * </p>
   *
   * @param fn a {@link java.util.function.BiConsumer} object.
   */
  void allowedAttributes(BiConsumer<String, List<String>> fn);

  /**
   * <p>
   * <em>fn</em> will be called once for each allowed tag
   * with the enforced attributes map as the second argument, for
   * example <em>accept('a',{ 'rel':'nofollow' })</em>
   * </p>
   *
   * @param fn a {@link java.util.function.BiConsumer} object.
   */
  void enforcedAttributes(BiConsumer<String, Map<String, String>> fn);

  /**
   * <p>
   * <em>fn</em> will be called once for each allowed tag
   * with the allowed protocols as the second argument, for
   * example <em>accept('a',{ 'href':['http','https'] })</em>
   * </p>
   *
   * @param fn a {@link java.util.function.BiConsumer} object.
   */
  void allowedProtocols(BiConsumer<String, Map<String, List<String>>> fn);

  /**
   * @param tagName (non null) a valid HTML tag name
   * @return true if <em>tagName</em> is allowed
   */
  boolean allowsTag(String tagName);

  /**
   * @param tagName (non null) a valid HTML tag name
   * @return true if <em>tagName</em> has allowed attributes.
   */
  boolean hasAllowedAttributes(String tagName);

  /**
   * @param tagName  (non null) a valid HTML tag name
   * @param attrName (non null) a valid HTML attribute name
   * @return true if <em>tagName</em> allows <em>attrName</em>
   */
  boolean allowsAttribute(String tagName, String attrName);

  /**
   * @param tagName (non null) a valid HTML tag name
   * @return true if <em>tagName</em> has any enforced attributes.
   */
  boolean hasEnforcedAttributes(String tagName);

  /**
   * @param tagName  (non null) a valid HTML tag name
   * @param attrName (non null) a valid HTML attribute name
   * @return true if <em>tagName</em> enforces a value for <em>attrName</em>
   */
  boolean enforcesAttribute(String tagName, String attrName);

  /**
   * @param tagName       (non null) a valid HTML tag name
   * @param attrName      (non null) a valid HTML attribute name
   * @param enforcedValue a {@link java.lang.String} object.
   * @return true if <em>tagName</em> enforces the value <em>enforcedValue</em> for <em>attrName</em>
   */
  boolean enforcesAttribute(String tagName, String attrName, String enforcedValue);

  /**
   * @param tagName (non null) a valid HTML tag name
   * @return true if <em>tagName</em> has any configured allowed protocols
   */
  boolean hasAllowedProtocols(String tagName);

  /**
   * @param tagName  (non null) a valid HTML tag name
   * @param attrName (non null) a valid HTML attribute name
   * @return true if <em>tagName</em>'s <em>attrName</em> attribute has allowed protocols.
   */
  boolean hasAllowedProtocols(String tagName, String attrName);

  /**
   * <p>allowsProtocol.</p>
   *
   * @param tagName  (non null) a valid HTML tag name
   * @param attrName (non null) a valid HTML attribute name
   * @param protocol (non null) a protocol like 'https' or 'http'
   * @return true if <em>tagName</em>'s <em>attrName</em> attribute allows <em>protocol</em>
   */
  boolean allowsProtocol(String tagName, String attrName, String protocol);

  /**
   * <p>Applies all the configured rules to the supplied {@link Whitelist} instance.</p>
   *
   * @param whitelist a {@link org.jsoup.safety.Whitelist} object.
   * @return a configured {@link org.jsoup.safety.Whitelist} instance.
   */
  Whitelist apply(Whitelist whitelist);

  /**
   * <p>
   * Constructs an empty {@link Whitelist} and applies the configured
   * rules to it.
   * </p>
   *
   * @return a configured {@link org.jsoup.safety.Whitelist} object.
   */
  Whitelist whitelist();

}
