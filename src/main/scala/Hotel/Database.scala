package Hotel

import Hotel.PrivateExecutor._
import slick.jdbc.GetResult
import slick.jdbc.MySQLProfile.api._

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object PrivateExecutor {
  val executor = Executors.newFixedThreadPool(4)
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executor)
}

object connection {
  // Define your database connection details
  val db = Database.forConfig("database")
}

object database {
  def ReadPlainQuery[T](tableName: String, query: String)(implicit getResult: GetResult[T]): Unit = {
    // Construct the SQL query
    val sqlQuery = sql"""#$query""".as[T]

    // Execute the query
    val futureResults: Future[Seq[T]] = connection.db.run(sqlQuery)

    // Handle the result asynchronously
    futureResults.onComplete {
      case Success(results) => println(s"Read $tableName:\n${results.mkString("\n")}")
      case Failure(ex) => println(s"Error reading $tableName because: $ex")
    }

    // Sleep and shutdown executor (as in your original code)
    Thread.sleep(1000)
    PrivateExecutor.executor.shutdown()
  }



}
