# Arithmetic using the Scala type system

Originally for a lightning talk 21/8/18

## Peano Axioms

1. Zero is a natural number
2. Given a natural number a, the successor of a is a number
3. Zero is not the successor of any number
4. If the successors of two numbers are equal, the numbers are equal
5. (Induction)

## Using the project

Install SBT and start a console with `sbt console`.

`import com.softwire.NaturalNumbers._` will put everything into scope.
You will also need to `import com.softwire.ToInt._` to use the `toInt` helper.

## Type Representation

We can represent these as types - see `NaturalNumbers.scala`

Operations are accessible through type projection - e.g. `_3#Add[_1]`

Aliases make this neater - e.g. `_3 + _1`

## Checking the Output

To check the result of a computation, we can search for an implicit `=:=` (from Scala Predef) which proves that two types are equal.
For example `implicitly[_3 + _1 =:= _4]` will verify that `3 + 1 = 4`.

If the implicit _can_ be found, the result is valid, otherwise it is not. For example `implicitly[_3 + _1 =:= _5]` will not compile.

Otherwise we must produce the value at runtime, which we can do using the `toInt` helper - e.g. `toInt[_3 + _1]` will return `4` at runtime.

## Limitations

As this makes heavy use of recursion it is very easy to break the compiler as it gets more difficult (usually with a `StackOverflowError`).

## Runtime Version

In RuntimeNat you can find a line-by-line translation into boring 'runtime' code.
