package io.shick.jsoup;

/**
 * <p>WhitelistConfigurationFormatter interface.</p>
 *
 * @author Trever Shick - trever@shick.io
 */
public interface WhitelistConfigurationFormatter {
  /**
   * <p>format.</p>
   *
   * @param config a {@link io.shick.jsoup.WhitelistConfiguration} object.
   * @return a {@link java.lang.CharSequence} object.
   */
  CharSequence format(WhitelistConfiguration config);
}
