package Hotel.Actors
import Hotel.Actors.BookingActor.{Book, Cancel, getUpcomingBookings}

import Hotel.CRUDs.BookingCRUD.{addBooking, cancelBooking, getUpcomingBooking}
import Hotel.CRUDs.CurrentlyReservedCRUD.{addReservation, cancelReservation, findAvailableRoom, isRoomReserved}
import Hotel.CRUDs.{BookingCRUD, CurrentlyReservedCRUD, RoomCRUD}
import Hotel.PrivateExecutor._
import Hotel.Models.Room.RoomTable
import Hotel.Models.{BookingClass, CurrentlyReservedClass, RoomClass}
import Hotel.connection.db
import akka.actor.{Actor, ActorRef, Props}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.{Duration, DurationInt}
import slick.jdbc.MySQLProfile.api._

import akka.util.Timeout


import java.time.LocalDate


object BookingActor{
  case class Book(roomCapacity: Int, startDate: LocalDate, endDate: LocalDate, guestId: Int)
  case class Cancel(reservationId: Int)
  case class getUpcomingBookings()
}


class BookingActor extends Actor {
  implicit val timeout: Timeout = Timeout(5.seconds)
  def receive: Receive = {
    case Book(roomCapacity: Int, startDate: LocalDate, endDate: LocalDate, guestId: Int) => {
      bookingHandeling(roomCapacity, startDate, endDate, guestId)
    }

    case Cancel(reservationId: Int) => {
      cancleReservation(reservationId)
    }

    case getUpcomingBookings() => {
      getFutureBookings()
    }
  }

  private def getFutureBookings() = {
    val result = Await.result(getUpcomingBooking(), 2.seconds)
    result.foreach {
      case (booking, guest) =>
        println(s"Booking ID: ${booking.id.getOrElse("N/A")}, Room ID: ${booking.roomId}, " +
          s"Start Date: ${booking.reservationStartDate}, End Date: ${booking.reservationEndDate}, " +
          s"Guest ID: ${booking.guestId}, " +
          s"Guest Name: ${guest.name}, Status: ${guest.status}, " +
          s"Email: ${guest.email}, Phone: ${guest.phone}")
    }
  }



  //----------------------------------------------BOOKING-LOGIC----------------------------------------------
  private def bookingHandeling(roomCapacity: Int, startDate: LocalDate, endDate: LocalDate, guestId: Int) = {
    val rooms = getAllRoomsByCapacity(roomCapacity)
    val result: Int = Await.result(Future(roomAvailable(roomCapacity, startDate, endDate)), Duration.Inf)

    if (result != -1) {
      val currentlyReservedClass: (Option[Int], Int, LocalDate, LocalDate, Int) = (None, result, startDate, endDate, guestId)

      val futureResponse = Await.result(Future(makeReservation(currentlyReservedClass)), timeout.duration)
      println(s"Reservation ID: $futureResponse")
    } else {
      println("No Rooms available at this period of time")
    }
  }

  private def roomAvailable(roomCapacity: Int, startDate: LocalDate, endDate: LocalDate): Int = {
    val rooms = getAllRoomsByCapacity(roomCapacity)
    val reservationResult: Future[Either[Boolean, Int]] = findAvailableRoom(rooms, startDate, endDate)

    val result: Int = Await.result(reservationResult, Duration.Inf) match {
      case Left(false) => -1
      case Right(roomId) => roomId
      case _ => 0
    }

    result
  }


  private def getAllRoomsByCapacity(capacity: Int): Seq[RoomClass] = {
    Await.result(RoomCRUD.getAllRoomsByCapacity(capacity), Duration.Inf)
  }

  private def makeReservation(currentlyReserved: (Option[Int], Int, LocalDate, LocalDate, Int)) = {
    println("MAKEING RESERVATION.....")
    val currentlyReservedBooking: BookingClass = BookingClass.tupled(currentlyReserved)
    val bookingResult = addBooking(currentlyReservedBooking)
    val bookingId = Await.result(bookingResult, 2.seconds)
    val temp: (Option[Int], Int, LocalDate, LocalDate, Int, Int) = (
      currentlyReserved._1, currentlyReserved._2, currentlyReserved._3, currentlyReserved._4, currentlyReserved._5, bookingId)
    val currentlyReservedClass: CurrentlyReservedClass = CurrentlyReservedClass.tupled(temp)
    val reservationResult = addReservation(currentlyReservedClass)

    val result = try {
      Await.result(reservationResult, 2.seconds) // Adjust the timeout duration as needed
      Await.result(bookingResult, 2.seconds) // Adjust the timeout duration as needed
    } catch {
      case e: Throwable =>
        println(s"Error waiting for result: $e")
    }
    result
  }

  //----------------------------------------------CANCELLING-LOGIC----------------------------------------------
  private def cancleReservation(reservationId : Int) = {
    println("CANCELING BOOKING.....")
    val cancellationResult = cancelReservation(reservationId)
    val cancelBookingResult = cancelBooking(reservationId)

    val result = try {
      Await.result(cancellationResult, 2.seconds) // Adjust the timeout duration as needed
      Await.result(cancelBookingResult, 2.seconds) // Adjust the timeout duration as needed
    } catch {
      case e: Throwable =>
        println(s"Error waiting for result: $e")
    }
    result
  }

}