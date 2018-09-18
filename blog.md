# Arithmetic using the Scala Type System

Most programs use _value_ programming - the code runs and uses functions to manipulate different values.
However, given a sufficiently powerful type system we can do _type_ programming - that is to say,
using the type system to perform a computation at compile time!

This blog post explores a simple implementation of Peano-style arithmetic using Scala types.

## Peano Arithmetic

To make sense of the program, it helps to know what we are trying to work towards
(but feel free to skip the maths if you like).

Peano arithmetic is a common way of defining the natural numbers (0, 1, 2...) and consists of the following axioms:

1. Zero is a natural number
2. Given a natural number `a`, the successor of `a` is a number
3. Zero is not the successor of any number
4. If the successors of two numbers are equal, the numbers are equal
5. Induction Axiom

We usually interpret the 'successor' of a number as 'adding one' -
so the successor of zero is one etc.:
```
S(0) = 1
S(1) = 2
S(2) = 3
...
```

The induction rule is very critical to the general theory of natural numbers
but is not required for this example, so we will skip over it (see below).

Now we will introduce the rules one-by-one, and see how they apply to our Scala types:

## The Nat type

Before we can add any rules, we need a way of identifying natural numbers:

```scala
class Nat {}
```

For those familiar with Scala, this could equally well be a trait:
we are only using it for its _type_ - we will never actually instantiate this class! 

### Zero

1. Zero is a natural number

Let's define a new type for it:

```scala
class _0 extends Nat {}
```

By definition our zero (`_0`) is a natural number.

(Scala classes cannot start with a number, so we prefix with an underscore)

### Successors

2. Given a natural number `a`, the successor of `a` is a number

Similarly, let's define a successor type:

```scala
class S[N <: Nat] extends Nat {}
```

If you aren't familiar with Scala, this says that we have a class `S` with a single type parameter `N`.
The `<:` symbol is a type bound, which ensures that `N` must be a subtype of `Nat`.

Therefore, this gives the successor of any natural number type `N`.

We can use our conventional names for successors of zero by using type aliases:

```scala
type _1 = S[_0]
type _2 = S[_1]
type _3 = S[_2]
// etc.
```

### The other rules

3. Zero is not the successor of any number

It is clear by definition that `_0` is not equal to `S[N]` (for any type of `N`).

4. If the successors of two numbers are equal, the numbers are equal

It follows from equality of parameterised types in Scala that `S[N]` is the same type as `S[M]` if and only if `N` and `M` are the same type.

5. Induction Axiom

Our Scala formulation does not actually have the Induction Axiom!
Unfortunately this means we cannot prove general theorems:
we can prove any individual statement, but are not able to prove statements about all numbers at once.

For example, we may prove that `3 * 5 = 15`, but we may not prove that (in general) `a * b = b * a`.

(Technically speaking this means we are using [Robinson arithmetic](https://en.wikipedia.org/wiki/Robinson_arithmetic) rather than Peano)

## Recursive addition

Now we have definitions which allow us to represent every natural number with a type:

```scala
class Nat {}
class _0 extends Nat {}
class S[N <: Nat] extends Nat {}

type _1 = S[_0]
type _2 = S[_1]
type _3 = S[_2]
// etc.
```

This is interesting, but we'd like to be able to _do_ something with these types!

To define the addition operation, we use the following recursive rules:
1. `0 + a = a`
2. `S(n) + a = S(n + a)`

Notice that the second rule reduces the size of the left operand, and so (for all standard numbers)
we can reduce down to zero by repeatedly applying rule 2, at which point we apply rule 1 and complete the addition.

### Addition Type

First, define the addition operation on a natural number
```scala
class Nat {
  type Add[A <: Nat] <: Nat
}
```
This says that each natural number has a [higher-kinded type](https://www.atlassian.com/blog/archives/scala-types-of-a-higher-kind)
that takes a single parameter `A` (which must be a natural number), and the type itself must be a natural number.

While a bit intimidating at first, this is just like an abstract method definition, but with types!
The value equivalent might look something like:
```scala
// Value level equivalent of the definition above
class Nat {
  def add(a: Nat): Nat
}
```

Now we need to actually define the type for each natural number.
The implementation for zero is straightforward: the first rule states that adding `A` to zero simply gives you `A`.
```scala
class _0 extends Nat {
  type Add[A <: Nat] = A
}
```

The implementation for successor is a little more interesting: the second rule states that we get the type from adding `S[A]` to `N`.
```scala
class S[N <: Nat] extends Nat {
  type Add[A <: Nat] = S[N#Add[A]]
}
```
The `#` operator is called ['type projection'](https://docs.scala-lang.org/overviews/quasiquotes/type-details.html#type-projection)
and allows us to access the `Add` type of `N`.
The value level equivalent would be using `.` to access members:

```scala
// Value level equivalent of the definition above
class S(n: Nat) extends Nat {
  def add(a: Nat) = new S(n.add(a))
}
``` 

Now we have a (fairly clunky) way of expressing additions using types. For example, the type of `_3` + `_4`:

```scala
_3#Add[_5]
```

### Implicitly and `=:=`

Before we can check that our shiny new addition operation actually works, we need a way of getting the compiler to check that two types are equal.
There are a number of ways to do this, but perhaps the neatest is using Scala's `implicitly` and `=:=` built-ins.

For example, to check that `S[S[_3]]` is `_5`:

```scala
implicitly[S[S[_3]] =:= _5]
```

This compiles, but if the types are not equal, we get a compilation error:

```scala
implicitly[S[_4] =:= _2]
```

```
Cannot prove that com.softwire.NaturalNumbers.S[com.softwire.NaturalNumbers._4] =:= com.softwire.NaturalNumbers._2
       implicitly[S[_4] =:= _2]
                 ^
```

The details of how this works are not too important, but they use a powerful feature of Scala called [implicits](https://docs.scala-lang.org/tour/implicit-parameters.html).
It is certainly possible to do these checks without them, but not quite as neatly.

### Calculating a sum

Now we are ready to perform a real calculation!
We can use the `implicitly` trick to check that our addition works:

```scala
implicitly[_3#Add[_5] =:= _8]
```

compiles fine, whereas an invalid sum:

```scala
implicitly[_3#Add[_5] =:= _9]
```
```
Cannot prove that com.softwire.NaturalNumbers.S[com.softwire.NaturalNumbers.S[com.softwire.NaturalNumbers.S[com.softwire.NaturalNumbers._5]]] =:= com.softwire.NaturalNumbers._9.
       implicitly[_3#Add[_5] =:= _9]
                 ^
```
We can see that the compiler has actually expanded the sum into a series of successors - which is exactly how the recursive definition of addition works!

Note that in both cases, we never actually need to run any code - we simply attempt to compile it!

### Filling in the gaps

The [completed source](https://github.com/Joe-Edwards/type-arithmetic/blob/master/src/main/scala/com/softwire/NaturalNumbers.scala)
fills in remaining arithmetic operations:
- Subtraction
- Multiplication
- Division

The last of these provides a bit of complexity as we want to be able to perform _inexact_ division.
This requires the notion of conditions and inequalities, as the base case applies to any number less than the numerator.

The simplest of these is the `IfZero` type:
```scala
class Nat {
  type IfZero[T <: Nat, F <: Nat] <: Nat
}

class _0 extends Nat {
  type IfZero[T <: Nat, F <: Nat] = T
}

class S[N <: Nat] extends Nat {
  type IfZero[T <: Nat, F <: Nat] = F
}
```
The type takes two parameters, and returns the first if the number is zero, or the second if the number is non-zero (it is a successor).
Have a look at the code to see how this can be used recursively to check if a number is less than another, and ultimately used to define division.

#### Aliases

For convenience, we also define some aliases that allow use to use familiar symbols:

```scala
  type +[A <: Nat, B <: Nat] = A#Add[B]
  type -[A <: Nat, B <: Nat] = B#SubFrom[A]
  type *[A <: Nat, B <: Nat] = A#Mult[B]
  type /[A <: Nat, B <: Nat] = A#Div[B]
  type %[A <: Nat, B <: Nat] = A - (B * (A / B))
```

Scala allows types with two parameters to be used 'infix' - letting us write `_3 * _5` as a valid type.

(Careful: it is usually a bad idea to use common symbols in this way,
but in this case it helps us see what is going on without getting too hung up on notation). 

#### Putting it into action

Now we can perform more complex calculations:

```scala
implicitly[((_3 * _5) + _1) / _2 =:= _8]
```

Compiles just fine, whereas
```scala
implicitly[((_3 * _5) + _1) / _2 =:= _7]
```
gives us a compilation error:
```
 Cannot prove that com.softwire.NaturalNumbers._3 * com.softwire.NaturalNumbers._5 + com.softwire.NaturalNumbers._1 / com.softwire.NaturalNumbers._2 =:= com.softwire.NaturalNumbers._7
       implicitly[((_3 * _5) + _1) / _2 =:= _7]
                 ^
```
(Note that all operators are left associative, so you need brackets for compound expressions to be evaluated properly)

#### Computation

As an example of a 'real' computation, we have also implemented the Euclidean algorithm for computing the Greatest Common Divisor of two numbers

```scala
implicitly[gcd[_12, _18] =:= _6]
```

At this point it should be easy to see how this may be extended to any other operations that can be defined recursively using the operations we already have.

### Limitations

Computing simple expressions is great, but this approach does suffer from a couple of significant limitations:

#### Knowing the answer

All the examples above are checking a computation is valid - a boolean output.
The fundamental problem is that if we discard the compilation result then all we know is whether or not compilation was successful.

To solve this, we can cheat a little. By defining an appropriate set of [implicits](https://docs.scala-lang.org/tour/implicit-parameters.html)
we can get the compiler to effectively 'fill-in' the answer as an `Int` value, which is simply returned at run-time.

The source for this can be found in [`ToInt.scala`](https://github.com/Joe-Edwards/type-arithmetic/blob/master/src/main/scala/com/softwire/ToInt.scala)
and it allows us to write code like:

```
scala> toInt[_3 * _7]
res1: Int = 21
``` 
The important thing to realise as that we are still performing the _calculation_ at compile time,
but we have to run the code to return the result.

#### Efficiency

There is another big problem - it gets very slow very quickly!
Large computations become noticeably sluggish and if the compiler has to work with a value too large (typically somewhere in the region of 1000-10000)
then it will crash with a `StackOverflowError`.

The compiler performs the type calculations by recursively determining the proper type of the parameters.
Each successor in the chain adds another level of depth to the type checker stack and eventually it runs out of memory and dies.

Of course, we can defer the problem by increasing the stack size given to the compiler, but this isn't particularly scalable -
at some point you'll hit a new limit!

If you really want to pursue this route, you can be a bit more efficient by storing numbers in binary and carefully defining your operations.
This allows you to perform calculations with much larger arguments (at the cost of simplicity),
for example by [using an HList style structure](https://apocalisp.wordpress.com/2010/06/24/type-level-programming-in-scala-part-5a-binary-numbers/),
where each node is either `Zero` or `One`.

## Uses

Of course, we've been able to perform basic arithmetic with computers for quite some time,
so this particular example is not particularly useful in isolation!
However, the ideas can be applied in targeted and useful ways - adding an extra level of safety at compilation time.

For example, when building a Finite State Machine you can encode the current state in its type
and use that type to ensure that only certain operations can be called in any particular state.
Type-level computations can be used to make more complex assertions about the state of the system.

## Further Reading

[Apocalisp - Type-Level Programming in Scala](https://apocalisp.wordpress.com/2010/06/08/type-level-programming-in-scala/)
is a series of blog posts on the same subject with a bit more theory.

[Shapeless](https://github.com/milessabin/shapeless) is a Scala library providing efficient implementations of type based programming and is used in many production systems.
