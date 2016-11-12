package io.shick.jsoup.jowli;

import io.shick.jsoup.BasicWhitelistConfiguration;

/**
 * <p>JowliMLWhitelistConfiguration class.</p>
 *
 * @author Trever Shick - trever@shick.io
 */
public class JowliMLWhitelistConfiguration extends BasicWhitelistConfiguration {

  /**
   * <p>Constructor for JowliMLWhitelistConfiguration.</p>
   */
  public JowliMLWhitelistConfiguration() {
  }

  /**
   * <p>toString.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String toString() {
    return new JowliMLFormatter().format(this).toString();
  }
}
