package com.softwire

// No need for higher kinds - this uses no types at all!

object RuntimeNat {

  // Natural Number class
  abstract class Nat extends Aliases {

    // Basic Operations
    def add(a: Nat): Nat
    def subFrom(a: Nat): Nat
    def mult(a: Nat): Nat
    def div(a: Nat): Nat

    // Greatest Common Divisor
    def gcd(a: Nat): Nat

    // Auxiliary operations
    def pre: Nat
    def ifZero(t: Nat, f: Nat): Nat
    def ifLt(a: Nat, t: Nat, f: Nat): Nat
  }

  // Zero singleton
  case object _0 extends Nat {
    def add(a: Nat): Nat = a
    def subFrom(a: Nat): Nat = a
    def mult(a: Nat): Nat = _0
    def div(a: Nat): Nat = _0
    
    def gcd(a: Nat): Nat = a
    
    def pre: Nat = null
    def ifZero(t: Nat, f: Nat): Nat = t
    def ifLt(a: Nat, t: Nat, f: Nat): Nat = a.ifZero(f, t)
  }

  // Successor class
  case class S(n: Nat) extends Nat {
    def add(a: Nat): Nat = n.add(S(a))
    def subFrom(a: Nat): Nat = n.subFrom(a.pre)
    def mult(a: Nat): Nat = a.add(a.mult(n))
    def div(a: Nat): Nat = S(n).ifLt(a, _0, S(a.subFrom(S(n)).div(a)))
    
    // Euclidean algorithm: gcd(a, b) = gcd(b, a mod b)
    def gcd(a: Nat): Nat = a.gcd(S(n) % a)
    
    def pre: Nat = n
    def ifZero(t: Nat, f: Nat): Nat = f
    def ifLt(a: Nat, t: Nat, f: Nat): Nat = a.ifZero(f, n.ifLt(a.pre, t, f))
  }
  
  // Basic Operations
  trait Aliases { a: Nat =>
    def +(b: Nat): Nat = a.add(b)
    def -(b: Nat): Nat = b.subFrom(a)
    def *(b: Nat): Nat = a.mult(b)
    def /(b: Nat): Nat = a.div(b)
    def %(b: Nat): Nat = a - (b * (a / b))
  }

  // Greatest Common Divisor
  def gcd(a: Nat, b: Nat): Nat = a.gcd(b)
  
  // Low number singletons
  val _1 = S(_0)
  val _2 = S(_1)
  val _3 = S(_2)
  val _4 = S(_3)
  val _5 = S(_4)
  val _6 = S(_5)
  val _7 = S(_6)
  val _8 = S(_7)
  val _9 = S(_8)
  val _10 = S(_9)
  val _11 = S(_10)
  val _12 = S(_11)
  val _13 = S(_12)
  val _14 = S(_13)
  val _15 = S(_14)
  val _16 = S(_15)
  val _17 = S(_16)
  val _18 = S(_17)
  val _19 = S(_18)
  val _20 = S(_19)
  val _21 = S(_20)
  val _22 = S(_21)
  val _23 = S(_22)
  val _24 = S(_23)
  val _25 = S(_24)

  // Recursive toInt
  def toInt(n: Nat): Int = n match {
    case S(m) => 1 + toInt(m)
    case _0 => 0
  }
}
