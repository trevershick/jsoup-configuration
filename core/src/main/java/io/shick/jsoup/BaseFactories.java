package io.shick.jsoup;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.jsoup.safety.Whitelist;

/**
 * Created by tshick on 11/17/16.
 */
public class BaseFactories {
  

  public static final Map<String, Supplier<Whitelist>> FACTORIES;

  public static final String BASIC = "basic";

  public static final String BASICWITHIMAGES = "basicwithimages";

  public static final String RELAXED = "relaxed";

  public static final String NONE = "none";
  static {
    Map<String,Supplier<Whitelist>> m = new HashMap<>();
    m.put(BASIC, Whitelist::basic);
    m.put(BASICWITHIMAGES, Whitelist::basicWithImages);
    m.put(RELAXED, Whitelist::relaxed);
    m.put(NONE, Whitelist::none);
    FACTORIES = Collections.unmodifiableMap(m);
  }

}
