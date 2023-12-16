package Hotel.Actors
import Hotel.Actors.RoomActor._
import Hotel.Models.Room.RoomTable
import Hotel.CRUDs.RoomCRUD._
import Hotel.PrivateExecutor._
import Hotel.Models.{BookingClass, RoomClass}
import Hotel.connection.db
import akka.actor.{Actor, ActorRef, Props, Status}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.{Duration, DurationInt}
import scala.util.{Failure, Success}
import slick.jdbc.MySQLProfile.api._

object RoomActor{
  case class getAllRoomsOfcapacity(capacity: Int)

}

class RoomActor extends Actor {
  var isBooked: Boolean = false

  def receive: Receive = {
    case getAllRoomsOfcapacity(capacity) => {
      val roomsFuture = Await.result(getAllRoomsByCapacity(capacity), 2.seconds)
//      val processedResult: Future[Unit] = roomsFuture.map { rooms =>
//        // Loop through the RoomClass instances
//        for (room <- rooms) {
//          // Access the id and do something with it
//          println(s"Room ID: ${room.id}")
//          // Your logic here
//        }
//      }

       // Blocking operation; consider handling asynchronously
//      println(Await.result(getAllRoomsByCapacity(capacity), 2.seconds))
      sender() ! roomsFuture
    }
//    case BookingClass(_, _, _, _, _) if isBooked =>
//      // Room is already booked
//      sender() ! "RoomUnavailable"
//    case booking: BookingClass =>
//      // Process booking request and update room state asynchronously
//      val originalSender = sender()
//      isBookingValidAsync(booking).onComplete {
//        case Success(valid) =>
//          if (valid) {
//            isBooked = true
//            originalSender ! "BookingSuccess"
//          } else {
//            originalSender ! "InvalidBooking"
//          }
//        case Failure(ex) =>
//          originalSender ! Status.Failure(ex)
//      }
  }

  private def isBookingValidAsync(booking: BookingClass): Future[Boolean] = {
    // Implement your asynchronous validation logic here
    // You can use db.run for database queries and return a Future[Boolean]
    // For example, check if the requested date range is valid
    // db.run(/* Your asynchronous validation query */).map(result => result.isValid)
    Future.successful(true)
  }


}

