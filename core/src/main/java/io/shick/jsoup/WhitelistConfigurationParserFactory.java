package io.shick.jsoup;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class WhitelistConfigurationParserFactory {
  private static final Logger LOG = Logger.getLogger("jsoup-configuration");
  private static final Map<String,Supplier<WhitelistConfigurationParser>> PARSERS = new HashMap<>();
  
  public static final Collection<String> registeredParserTypes() {
    return PARSERS.keySet();
  }
  
  public static void register(String type, Supplier<WhitelistConfigurationParser> parserSupplier) {
    LOG.fine("Registering parser factory of type " + type);
    
    PARSERS.put(requireNonNull(type, "type cannot be null"),
      requireNonNull(parserSupplier, "parserSupplier cannot be null"));
  }
  
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
