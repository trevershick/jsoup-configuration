package io.shick.jsoup.jowli.ast;

class ValueObject<T> {
  private final T value;

  /**
   * <p>Constructor for ValueObject.</p>
   *
   * @param t a T object.
   */
  public ValueObject(T t) {
    this.value = t;
  }

  T value() {
    return value;
  }

  /**
   * <p>toString.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String toString() {
    return value.toString();
  }
}
