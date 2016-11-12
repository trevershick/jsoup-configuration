package io.shick.jsoup;

import java.text.ParseException;

/**
 * <p>WhitelistConfigurationParser interface.</p>
 *
 * @author Trever Shick - trever@shick.io
 */
public interface WhitelistConfigurationParser {
  /**
   * <p>parse.</p>
   *
   * @param value (non null)
   * @return a {@link io.shick.jsoup.WhitelistConfiguration} instance
   * @throws java.lang.NullPointerException if value is null
   * @throws java.text.ParseException       if value is un-parseable
   */
  WhitelistConfiguration parse(CharSequence value) throws
    NullPointerException,
    ParseException;
}
