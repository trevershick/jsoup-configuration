package io.shick.jsoup;

/**
 * <p>MutableWhitelistConfiguration interface.</p>
 *
 * @author Trever Shick - trever@shick.io
 */
public interface MutableWhitelistConfiguration extends WhitelistConfiguration {
  /**
   * <p>Add an allowed tag to the configuration</p>
   *
   * @param tagName (non null) an HTML tag name
   * @return a {@link io.shick.jsoup.MutableWhitelistConfiguration} object.
   */
  MutableWhitelistConfiguration allowTag(String tagName);

  /**
   * <p>Add an enforced attribute value to the configuration</p>
   *
   * @param tagName       (non null) an HTML tag name
   * @param attrName      (non null) an HTML attribute name
   * @param enforcedValue (non null) the string value for the specified attribute.
   * @return a {@link io.shick.jsoup.MutableWhitelistConfiguration} object.
   */
  MutableWhitelistConfiguration enforceAttribute(String tagName, String attrName, String enforcedValue);

  /**
   * <p>Add an allowed protocol to the configuration</p>
   *
   * @param tagName  (non null) an HTML tag name
   * @param attrName (non null) an HTML attribute name
   * @param protocol (non null) a protocol like 'https' or 'http'
   * @return a {@link io.shick.jsoup.MutableWhitelistConfiguration} object.
   */
  MutableWhitelistConfiguration allowProtocol(String tagName, String attrName, String protocol);

  /**
   * <p>Add an allowed attribute to the configuration</p>
   *
   * @param tagName  (non null) an HTML tag name
   * @param attrName (non null) an HTML attribute name
   * @return a {@link io.shick.jsoup.MutableWhitelistConfiguration} object.
   */
  MutableWhitelistConfiguration allowAttribute(String tagName, String attrName);
}
