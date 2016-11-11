package io.shick.jsoup;

import org.jsoup.safety.Whitelist;

public interface WhitelistConfiguration {
  Whitelist apply(Whitelist in);

  Whitelist whitelist();

  String format(WhitelistConfiguration config);
}
