package Hotel.Actors
import Hotel.PrivateExecutor._
import Hotel.Models.BookingClass
import akka.actor.{Actor, Status}

import scala.concurrent.Future
import scala.util.{Failure, Success}

object RoomActor{

}

class RoomActor extends Actor {
  var isBooked: Boolean = false

  def receive: Receive = {
    case BookingClass(_, _, _, _, _) if isBooked =>
      // Room is already booked
      sender() ! "RoomUnavailable"
    case booking: BookingClass =>
      // Process booking request and update room state asynchronously
      val originalSender = sender()
      isBookingValidAsync(booking).onComplete {
        case Success(valid) =>
          if (valid) {
            isBooked = true
            originalSender ! "BookingSuccess"
          } else {
            originalSender ! "InvalidBooking"
          }
        case Failure(ex) =>
          originalSender ! Status.Failure(ex)
      }
  }

  private def isBookingValidAsync(booking: BookingClass): Future[Boolean] = {
    // Implement your asynchronous validation logic here
    // You can use db.run for database queries and return a Future[Boolean]
    // For example, check if the requested date range is valid
    // db.run(/* Your asynchronous validation query */).map(result => result.isValid)
    Future.successful(true)
  }
}

