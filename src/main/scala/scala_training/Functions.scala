package scala_training

import scala.annotation.tailrec

object Functions extends App {
  def aRepeatedFunction(aString: String, n: Int): String ={
    if(n==1)aString
    else aString + aRepeatedFunction(aString, n-1)
  }
  println(aRepeatedFunction("c",3))
  def aFunctionSideEffects(aString: String): Unit = println(aString)
  def aBigFunction (n: Int): Int ={
    def aSmallFunction(a: Int, b: Int): Int = a+b
    aSmallFunction(n, n-1)
  }
  def greeting (name: String, age: Int) = {
    println(s"hi my name is ${name} and I am ${age} years old")
  }
  def factorial (n: Int): Int ={
    if(n==1) return 1
    else n * factorial(n-1)
  }
  def fibonacciNumbers(n: Int): Int ={
    if (n <=1)1
    else fibonacciNumbers(n-1) + fibonacciNumbers(n-2)
  }

  def anotherFactorial (n: Int): Int ={
    //auxiliary funct
    def factHelper(x: Int, accumulator:Int): Int =
     if(x <= 1) accumulator
     else factHelper(x-1, accumulator * x)
    factHelper(n,1)
  }

  def isPrime (n:Int): Boolean ={
    @tailrec
    def isPrimeTailRec(t: Int, isStillPrime: Boolean): Boolean ={
      if(!isStillPrime)false
      else if(t <= 1) true
      else {
        println("t is : " + t)
        isPrimeTailRec(t-1, n % t!=0 && isStillPrime )
      }
    }
    isPrimeTailRec(n/2, true)
  }
  @tailrec
  def concatenate(x:Int, y: String, accumulator:String  = ""): String ={
    if(x==0)  accumulator
    else concatenate(x-1,y,accumulator+y)
  }
  def fibonacci(n: Int): Int ={
    def fibonacciTailRec(x:Int, last: Int, nextToLast: Int): Int ={
      if (x >= n) last
      else fibonacciTailRec(x+1 , nextToLast+last , last)
    }
    if(n<=2)1
    else fibonacciTailRec(2,1,1)
  }

  greeting("cenk",27)
  println(factorial(3))
  println(anotherFactorial(10))
  println(concatenate(3,"c",accumulator="as"))
  println(isPrime(11))
  println(fibonacci(8))
}
