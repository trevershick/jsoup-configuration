package io.shick.jsoup;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * <p>WhitelistConfigurationParserFactory class.</p>
 *
 * @author Trever Shick - trever@shick.io
 */
public class WhitelistConfigurationParserFactory {
  private static final Logger LOG = Logger.getLogger("jsoup-configuration");
  private static final Map<String, Supplier<WhitelistConfigurationParser>> PARSERS = new HashMap<>();

  /**
   * <p>registeredParserTypes.</p>
   *
   * @return a {@link java.util.Collection} object.
   */
  public static final Collection<String> registeredParserTypes() {
    return PARSERS.keySet();
  }

  /**
   * <p>register.</p>
   *
   * @param type           a {@link java.lang.String} object.
   * @param parserSupplier a {@link java.util.function.Supplier} object.
   */
  public static void register(String type, Supplier<WhitelistConfigurationParser> parserSupplier) {
    LOG.fine("Registering parser factory of type " + type);

    PARSERS.put(requireNonNull(type, "type cannot be null"),
      requireNonNull(parserSupplier, "parserSupplier cannot be null"));
  }

  /**
   * <p>newParser.</p>
   *
   * @param type a {@link java.lang.String} object.
   * @return a {@link io.shick.jsoup.WhitelistConfigurationParser} object.
   */
  public static final WhitelistConfigurationParser newParser(String type) {
    final Supplier<WhitelistConfigurationParser> factory = PARSERS.get(requireNonNull(type, "type cannot be null"));
    if (factory == null) {
      throw new IllegalArgumentException(type + " is not a registered parser type.");
    }
    return factory.get();
  }

  static {
    // TODO - replace with some sort of scanning
    Consumer<String> load = (clazz) -> {
      try {
        Class.forName(clazz);
      }
      catch (ClassNotFoundException e) {
      }
    };

    Stream.of(
      "io.shick.jsoup.gson.GsonParser",
      "io.shick.jsoup.jowli.JowliMLParser").forEach(load);
  }
}
