package io.shick.jsoup.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Func {

  /**
   * Append the two lists.
   * @param l1 the starting list
   * @param l2 the list to append
   * @return
   */
  public static final <T> List<T> concat(final List<T> l1, final List<T> l2) {
    if (empty(l1) && empty(l2)) { return new ArrayList(0); }
    if (!empty(l1) && empty(l2)) { return new ArrayList(l1); }
    if (!empty(l2) && empty(l1)) { return new ArrayList(l2); }
    
    final ArrayList<T> a = new ArrayList<>(l1.size() + l2.size());
    a.addAll(l1);
    a.addAll(l2);
    return a;
  }

  public static <T> boolean empty(Collection<T> l1) {
    return l1 == null || l1.isEmpty();
  }

  public static final Map<String, String> merge1(Map<String, String> l, Map<String, String> r) {
    if (empty(l) && empty(r)) { return new HashMap(); }
    if (!empty(l) && empty(r)) { return new HashMap(l); }
    if (!empty(r) && empty(l)) { return new HashMap(r); }

    Map<String, String> m = new HashMap();
    m.putAll(l);
    m.putAll(r);
    return m;
  }

  public static <K,V> boolean empty(Map<K, V> m) {
    return m == null || m.isEmpty();
  }

  public static final Map<String, List<String>> merge2(Map<String, List<String>> l, Map<String, List<String>> r) {
    if (empty(l) && empty(r)) { return new HashMap(); }
    if (!empty(l) && empty(r)) { return new HashMap(l); }
    if (!empty(r) && empty(l)) { return new HashMap(r); }

    Map<String, List<String>> m = new HashMap();
    m.putAll(l);
    r.keySet().forEach(k -> {
      m.merge(k, r.get(k), Func::concat);
    });
    return m;
  }

  public static final <K, V> Map<K, V> hashMap(K k, V v) {
    Map<K, V> m = new HashMap();
    m.put(k, v);
    return m;
  }

  public static final <T> List<T> list(T... values) {
    if (values == null) return new ArrayList(0);
    return new ArrayList<>(Stream.of(values).filter(Objects::nonNull).collect(Collectors.toList()));
  }

}
