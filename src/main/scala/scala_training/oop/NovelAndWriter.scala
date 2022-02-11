package scala_training.oop

object NovelAndWriter extends App{
  val author = new Writer("Elif" , "Şafak",1961)
  val novel = new Novel("aşk",2010,author)
  val novelCopy = novel.copy(2020)
  println(novel.authorAge)
  println(novelCopy)
  println(novel)
  val counter = new Counter(2)
  counter.inc.print
}
class Writer(name:String,surname:String, val year:Int){
  def fullname() = this.name + " " + this.surname
}

class Novel(name:String, val year:Int, author:Writer){
  val authorAge = author.year - this.year
  def isWrittenBy(author:Writer): Boolean = author == this.author
  def copy(newYear: Int): Novel =new Novel(name, newYear, author)
}

class Counter(val count:Int ){
  def inc = new Counter (count+1)
  def dec = new Counter (count-1)

  def inc (n:Int): Counter = {
   if(n<=0)this
   else inc.inc(n-1)
  }

  def dec(n:Int):Counter = {
    if(n<=0)this
    else dec.dec(n-1)
  }
  def print= println(count)
}