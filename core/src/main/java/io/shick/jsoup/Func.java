package io.shick.jsoup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by tshick on 11/10/16.
 */
public class Func {

  public static final List<String> conj(List<String> l, List<String> r) {
    final ArrayList<String> a = new ArrayList<>(l.size() + r.size());
    a.addAll(l);
    a.addAll(r);
    return a;
  }

  public static final Map<String, String> merge1(Map<String, String> l, Map<String, String> r) {
    Map<String, String> m = new HashMap();
    m.putAll(l);
    m.putAll(r);
    return m;
  }

  public static final Map<String, List<String>> merge2(Map<String, List<String>> l, Map<String, List<String>> r) {
    Map<String, List<String>> m = new HashMap();
    m.putAll(l);
    r.keySet().forEach(k -> {
      m.merge(k, r.get(k), Func::conj);
    });
    return m;
  }

  public static final <K, V> Map<K, V> hashMap(K k, V v) {
    Map<K, V> m = new HashMap();
    m.put(k, v);
    return m;
  }

  public static final <T> List<T> list(T ... values) {
    final List linkedList = new LinkedList();
    linkedList.addAll(Arrays.asList(values));
    return linkedList;
  }
}
