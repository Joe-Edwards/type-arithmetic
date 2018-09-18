package com.softwire

import scala.language.higherKinds

object Demo {

  class Nat {
    type Add[A <: Nat] <: Nat
  }

  class _0 extends Nat {
    type Add[A <: Nat] = A
  }

  class S[N <: Nat] extends Nat {
    type Add[A <: Nat] = S[N#Add[A]]
  }

  // Type aliases for low numbers
  type _1 = S[_0]
  type _2 = S[_1]
  type _3 = S[_2]
  type _4 = S[_3]
  type _5 = S[_4]
  type _6 = S[_5]
  type _7 = S[_6]
  type _8 = S[_7]
  type _9 = S[_8]
  type _10 = S[_9]
  type _11 = S[_10]
  type _12 = S[_11]
  type _13 = S[_12]
  type _14 = S[_13]
  type _15 = S[_14]
  type _16 = S[_15]
  type _17 = S[_16]
  type _18 = S[_17]
  type _19 = S[_18]
  type _20 = S[_19]
  type _21 = S[_20]
  type _22 = S[_21]
  type _23 = S[_22]
  type _24 = S[_23]
  type _25 = S[_24]

}
