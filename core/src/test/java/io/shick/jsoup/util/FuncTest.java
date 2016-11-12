package io.shick.jsoup.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class FuncTest {
  @Test
  public void concat() throws Exception {
    assertThat(Func.concat(null,null), is(equalTo(new ArrayList())));
    assertThat(Func.concat(null,Func.list("a")), is(equalTo(Func.list("a"))));
    assertThat(Func.concat(Func.list("a"),null), is(equalTo(Func.list("a"))));
    assertThat(Func.concat(Func.list(1,2,3),Func.list(4,5,6)), is(equalTo(Func.list(1,2,3,4,5,6))));
  }

  @Test
  public void merge1() throws Exception {
    Map<String, String> v1= Func.hashMap("3","4");
    Map<String, String> v2 = Func.hashMap("1","2");

    Map<String, String> actual = Func.merge1(v1, v2);
    assertThat(actual.size(),is(2));
    assertThat(actual.containsKey("3"), is(true));
    assertThat(actual.containsKey("1"), is(true));

    actual = Func.merge1(v1, null);
    assertThat(actual.size(),is(1));
    assertThat(actual.containsKey("3"), is(true));

    actual = Func.merge1(null, v2);
    assertThat(actual.size(),is(1));
    assertThat(actual.containsKey("1"), is(true));

    actual = Func.merge1(null, null);
    assertThat(actual.size(),is(0));
  }

  @Test
  public void merge2() throws Exception {
    Map<String, List<String>> v1= Func.hashMap("3",Func.list("a","b"));
    Map<String, List<String>> v2 = Func.hashMap("1",Func.list("c","d"));
    Map<String, List<String>> v3 = Func.hashMap("3",Func.list("c","d"));

    Map<String,  List<String>> actual = Func.merge2(v1, v2);
    assertThat(actual.size(),is(2));
    assertThat(actual.containsKey("3"), is(true));
    assertThat(actual.containsKey("1"), is(true));

    actual = Func.merge2(v1, null);
    assertThat(actual.size(),is(1));
    assertThat(actual.containsKey("3"), is(true));

    actual = Func.merge2(null, v2);
    assertThat(actual.size(),is(1));
    assertThat(actual.containsKey("1"), is(true));

    actual = Func.merge2(null, null);
    assertThat(actual.size(),is(0));

    actual = Func.merge2(v1, v3);
    assertThat(actual.size(),is(1));
    assertThat(actual.containsKey("3"), is(true));
    assertThat(actual.get("3"), is(Func.list("a","b","c","d")));
  }

  @Test
  public void hashMap() throws Exception {

  }

  @Test
  public void list() throws Exception {
    final ArrayList expected = new ArrayList();
    expected.add(1);
    expected.add(2);
    expected.add(3);

    assertThat(Func.list(1,2,3), is(expected));
    
    assertThat(Func.list(), is(new ArrayList()));
    assertThat(Func.list(null), is(new ArrayList()));
    assertThat(Func.list(null,null,null), is(new ArrayList()));
  }
}
