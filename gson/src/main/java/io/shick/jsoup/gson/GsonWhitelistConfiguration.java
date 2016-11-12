package io.shick.jsoup.gson;

import io.shick.jsoup.BasicWhitelistConfiguration;

/**
 * <p>GsonWhitelistConfiguration class.</p>
 *
 * @author Trever Shick - trever@shick.io
 */
public class GsonWhitelistConfiguration extends BasicWhitelistConfiguration {

  /**
   * <p>Constructor for GsonWhitelistConfiguration.</p>
   */
  protected GsonWhitelistConfiguration() {
  }

  /**
   * <p>toString.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String toString() {
    return new GsonFormatter().format(this).toString();
  }
}
  
