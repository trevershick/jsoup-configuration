package io.shick.jsoup;

import java.text.ParseException;

public interface WhitelistConfigurationParser {
  /**
   * @param value (non null)
   * @return a {@link WhitelistConfiguration} instance
   * @throws NullPointerException if value is null
   * @throws java.text.ParseException if value is un-parseable
   */
  WhitelistConfiguration parse(CharSequence value) throws
    NullPointerException,
    ParseException;

}
