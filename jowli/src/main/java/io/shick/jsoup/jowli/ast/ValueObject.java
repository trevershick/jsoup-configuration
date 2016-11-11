package io.shick.jsoup.jowli.ast;

class ValueObject<T> {
  private final T value;

  public ValueObject(T t) {
    this.value = t;
  }

  T value() {
    return value;
  }

  public String toString() {
    return value.toString();
  }
}
