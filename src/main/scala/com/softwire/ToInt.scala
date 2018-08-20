package com.softwire

import com.softwire.NaturalNumbers._

trait ToInt[N <: Nat] {
  def value: Int
}

object ToInt {
  implicit val zero: ToInt[_0] = new ToInt[_0] {
    def value: Int = 0
  }

  implicit def successor[N <: Nat](implicit nToInt: ToInt[N]): ToInt[S[N]] = new ToInt[S[N]] {
    def value: Int = nToInt.value + 1
  }

  def toInt[N <: Nat](implicit toInt: ToInt[N]): Int = toInt.value
}
