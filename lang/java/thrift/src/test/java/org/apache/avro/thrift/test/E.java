/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.apache.avro.thrift.test;

public enum E implements org.apache.thrift.TEnum {
  X(1), Y(2), Z(3);

  private final int value;

  private E(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  @Override
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * 
   * @return null if the value is not found.
   */
  public static E findByValue(int value) {
    switch (value) {
    case 1:
      return X;
    case 2:
      return Y;
    case 3:
      return Z;
    default:
      return null;
    }
  }
}
