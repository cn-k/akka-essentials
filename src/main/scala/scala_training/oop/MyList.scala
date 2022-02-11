package scala_training.oop

import scala_training.oop.Generics.Animal

import scala.collection.immutable.Stream.Empty

abstract class MyList[+A] {
  def head : A

  def tail : MyList[A]

  def isEmpty: Boolean

  def add[B>:A](elem: B): MyList[B]

  def printElements : String

  override def toString: String = "[" + printElements + "]"

  def map[B](transformer: A => B):MyList[B]
  def flatMap[B](transformer: A => MyList[B]):MyList[B]
  def filter(predicate:A =>Boolean):MyList[A]
  def ++[B >: A ](list:MyList[B]):MyList[B]
}

object Empty extends MyList[Nothing]{
  override def head: Nothing = throw new NoSuchElementException

  override def tail: MyList[Nothing] = throw new NoSuchElementException

  override def isEmpty: Boolean = true

  override def add[B >: Nothing](elem: B): MyList[B] = new Cons(elem, Empty)

  override def printElements: String = ""

  override def map[B](transformer: Nothing => B): MyList[B] = Empty

  override def flatMap[B](transformer: Nothing => MyList[B]): MyList[B] = Empty

  override def filter(predicate: Nothing => Boolean): MyList[Nothing] = Empty

  def ++[B >: Nothing](list:MyList[B]):MyList[B] = list
}

class Cons[+A](h: A, t: MyList[A]) extends MyList[A]{
  override def head: A = h

  override def tail: MyList[A] = t

  override def isEmpty: Boolean = false

  override def add[B >: A](elem: B): MyList[B] = new Cons(elem, this)

  override def printElements: String = {
    if (tail != Empty) h.toString +" " + tail.printElements
    else  h.toString
  }
  def map[B](transformer: A => B): MyList[B]={
    new Cons(transformer(h),t.map(transformer))
  }

  def flatMap[B](transformer: A => MyList[B]): MyList[B] = transformer(h) ++  t.flatMap(transformer)

  def filter (predicate: A => Boolean)={
    if(predicate(h))new Cons(h, t.filter(predicate))
    else t.filter(predicate)
  }

  override def ++[B >: A](list: MyList[B]): MyList[B] = new Cons(h, t ++ list)
}
/*
trait MyPredicate [-T]{
  def test (elem:T) : Boolean
}
trait MyTransformer[-A,B]{
  def transform(elem: A):B
}
 */
class Animal(val name:String){
  def greet() = s"hi I am animal"
}
class Cat(name:String) extends Animal(name){ override def greet = "hi I am cat"}
class Dog(name:String) extends Animal(name){ override def greet = "hi I am cat"}

object ListTest extends App{
  val listOfInteger:  MyList[Int] = new Cons(1,new Cons(2, new Cons(3, Empty)))
  val anotherListOfInteger:  MyList[Int] = new Cons(1,new Cons(4, new Cons(5, Empty)))
  val listOfString:  MyList[String] = new Cons("cenk",new Cons("ali", new Cons("mehmet", Empty)))

  val listOfAnimal: MyList[Animal] = new Cons(new Cat("tekir"), Empty)
  println(listOfAnimal.head.greet())
  println(listOfAnimal.toString)
  println(listOfInteger.toString)
  println(listOfString.toString)

  println(listOfInteger.map(elem => elem*2))

  println(listOfInteger.filter(elem => elem % 2 ==0))
  println(listOfInteger ++ anotherListOfInteger)

  println(listOfInteger.flatMap(elem => new Cons(elem, new Cons(elem+1, Empty))))

}