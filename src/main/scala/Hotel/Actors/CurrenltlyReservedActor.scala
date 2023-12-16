package Hotel.Actors
import Hotel.Actors.CurrenltReservedActor._
import Hotel.CRUDs.CurrentlyReservedCRUD._
import Hotel.PrivateExecutor._
import Hotel.Models.Room.RoomTable
import Hotel.Models.{BookingClass, CurrentlyReservedClass, RoomClass}
import Hotel.connection
import akka.actor.Actor

import scala.util.{Failure, Success}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.{Duration, DurationInt}
import slick.jdbc.MySQLProfile.api._

import java.time.LocalDate

object CurrenltReservedActor{
  case class isReserved(roomId: Int, startDate: LocalDate, endDate: LocalDate)
  case class findAvailableRoom(rooms: Seq[RoomClass], startDate: LocalDate, endDate: LocalDate)
  case class makeReservation(currentlyReserved: CurrentlyReservedClass)

}
class CurrenltReservedActor extends Actor {
//  override def receive: Receive = {
//    case checkAvailability(roomId, startDate, endDate) => {
//
//      val result = isRoomReserved(roomId, startDate, endDate)
//
//      result.onComplete {
//        case Success(isReserved) =>
////          print(s"Room $roomId availability result: $isReserved")
//          sender() ! isReserved
//
//        case Failure(ex) =>
////          println(s"Error checking room availability: $ex")
//          sender() ! false
//      }
//      Thread.sleep(1000)
//
//
//    }
//
//  }

  // --------- AWAIT ----------

  override def receive: Receive = {
    case makeReservation(currentlyReserved) => {
      println("makeReservation")
      val resultFuture = addReservation(currentlyReserved)

      val result = try {
        Await.result(resultFuture, 2.seconds) // Adjust the timeout duration as needed
      } catch {
        case e: Throwable =>
          println(s"Error waiting for result: $e")
      }

    }
    case isReserved(roomId, startDate, endDate) =>
      val resultFuture = isRoomReserved(roomId, startDate, endDate)

      val result = try {
        Await.result(resultFuture, 2.seconds) // Adjust the timeout duration as needed
      } catch {
        case e: Throwable =>
          println(s"Error waiting for result: $e")

      }
      sender() ! result

      case findAvailableRoom(rooms, startDate, endDate) =>
        // Create a Future[Boolean] for each room and collect them into a Seq[Future[Boolean]]
        val roomChecks: Seq[Future[Boolean]] = rooms.map(room => isRoomReserved(room.id, startDate, endDate))

        // Use Future.sequence to transform Seq[Future[Boolean]] into Future[Seq[Boolean]]
        val allRoomChecks: Future[Seq[Boolean]] = Future.sequence(roomChecks)

        // Use map to process the result when all the room checks are completed
        allRoomChecks.map { results =>
          // Find the index of the first false result
          results.indexOf(false) match {
            case index if index != -1 => Right(sender() ! rooms(index).id) // Room is available, return its id
            case _ => Left(sender() ! false) // All rooms are reserved
          }
        }

  }
}
