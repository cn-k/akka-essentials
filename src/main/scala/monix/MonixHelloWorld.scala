package monix

// We need a scheduler whenever asynchronous
// execution happens, substituting your ExecutionContext
import monix.execution.Scheduler.Implicits.global

// Needed below
import scala.concurrent.Await
import scala.concurrent.duration._

object MonixHelloWorld extends App{
  import monix.eval._

  // A specification for evaluating a sum,
  // nothing gets triggered at this point!
  val task = Task { 1 + 1 }

  // Actual execution, making use of the Scheduler in
  // our scope, imported above
  val future = task.runToFuture
  // future: monix.execution.CancelableFuture[Int] = monix.execution.CancelableFuture$Pure@7c744c8e

  future.foreach(println)
}
