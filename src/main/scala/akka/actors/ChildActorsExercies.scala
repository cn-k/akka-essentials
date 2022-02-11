package akka.actors

import ChildActorsExercies.WordCounterMaster.{Initialize, WordCountReply, WordCountTask}
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActorsExercies extends App{

  object WordCounterMaster{
    case class Initialize(nChildren:Int)
    case class WordCountTask(id: Int, text:String)
    case class WordCountReply(id: Int, count:Int)
  }
  class WordCountMaster extends Actor {
    override def receive: Receive = {
      case Initialize(count) =>
        println("[master] initializing")
        val childrenRefs = for(n <- 1 to count) yield context.actorOf(Props[WordCountWorker], s"wcw_$n")
        context.become(withChildren(childrenRefs,0, 0, Map()))

    }
    def withChildren(childrenRefs: Seq[ActorRef], currentChildIndex: Int, currentTaskId: Int, requestMap:Map[Int,
      ActorRef]):Receive={
      case text: String =>
        println(s"[master] I have received $text - I'll send it to child $currentChildIndex")
        val origSender  = sender()
        val task = WordCountTask(currentTaskId, text)
        val childRef = childrenRefs(currentChildIndex)
        childRef ! task
        val nextChildIndex = (currentChildIndex + 1) % childrenRefs.length
        val newTaskId = currentTaskId + 1
        val newRequestMap = requestMap + (currentTaskId -> origSender)
        context.become((withChildren(childrenRefs,nextChildIndex, newTaskId,newRequestMap)))
      case WordCountReply(id, count) =>
        println(s"I have received a reply for task id $id with count $count")
        val originalSender = requestMap(id)
        originalSender ! count
        context.become(withChildren(childrenRefs,currentChildIndex,currentTaskId,requestMap-id))

    }
  }
  class WordCountWorker extends Actor {
    override def receive: Receive = {
      case WordCountTask(id, text) =>
        println(s"${self.path} I have received a task with $id with $text")
        sender() ! WordCountReply(id, text.split( " ").length)
    }
  }
  class TestActor extends Actor{
    import WordCounterMaster._
    override def receive: Receive = {
      case "go" =>
        val master = context.actorOf(Props[WordCountMaster], "master")
        master ! Initialize(3)
        val texts = List("I love Akka","Scala is super dope", "yes", "me too")
        texts.foreach(text => master ! text)
      case count:Int =>
        println(s"test actor]I received a reply $count")
    }
  }
  val system = ActorSystem("RoundRobinCountExercise")
  val testActor = system.actorOf(Props[TestActor], "testActor")
  testActor ! "go"
}
