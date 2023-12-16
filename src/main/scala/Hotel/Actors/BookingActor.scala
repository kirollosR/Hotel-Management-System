package Hotel.Actors
import Hotel.Actors.BookingActor.{Book, Cancel}
import Hotel.Actors.CurrenltReservedActor.makeReservation
import Hotel.CRUDs.BookingCRUD.{addBooking, cancelBooking}
import Hotel.CRUDs.CurrentlyReservedCRUD.{addReservation, cancelReservation, findAvailableRoom, isRoomReserved}
import Hotel.CRUDs.{CurrentlyReservedCRUD, RoomCRUD}
import Hotel.PrivateExecutor._
import Hotel.Models.Room.RoomTable
import Hotel.Models.{BookingClass, CurrentlyReservedClass, RoomClass}
import Hotel.connection.db
import akka.actor.{Actor, ActorRef, Props}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.{Duration, DurationInt}
import slick.jdbc.MySQLProfile.api._
import Hotel.Main._
import akka.util.Timeout
import akka.pattern.ask

import java.time.LocalDate
import scala.:+
import scala.util.{Failure, Success}

object BookingActor{
  case class Book(roomCapacity: Int, startDate: LocalDate, endDate: LocalDate, guestId: Int)
  case class Cancel(reservationId: Int)
}


class BookingActor extends Actor {
  implicit val timeout: Timeout = Timeout(5.seconds)

  // Fetch rooms from the database and create RoomActors for each
  def getAllRoomActors: Map[Int, ActorRef] = {
    val roomsFuture: Future[Seq[RoomClass]] = db.run(RoomTable.result)

    val roomActorsFuture: Future[Map[Int, ActorRef]] = roomsFuture.map { rooms =>
      rooms.map { room =>
        room.id -> context.actorOf(Props[RoomActor], s"room${room.id}")
      }.toMap
    }

    Await.result(roomActorsFuture, Duration.Inf) // Blocking operation; consider handling asynchronously
  }

  def receive: Receive = {
    case Book(roomCapacity: Int, startDate: LocalDate, endDate: LocalDate, guestId: Int) => {
      bookingHandeling(roomCapacity, startDate, endDate, guestId)
    }

    case Cancel(reservationId: Int) => {
      cancleReservation(reservationId)
    }
  }



  //----------------------------------------------BOOKING-LOGIC----------------------------------------------
  private def bookingHandeling(roomCapacity: Int, startDate: LocalDate, endDate: LocalDate, guestId: Int) = {
    val rooms = getAllRoomsByCapacity(roomCapacity)
    val result: Int = Await.result(Future(roomAvailable(roomCapacity, startDate, endDate)), Duration.Inf)
    //    println(result)
    if (result != -1) {
      //      println(s"Room $result is available")
      val currentlyReservedClass: (Option[Int], Int, LocalDate, LocalDate, Int) = (None, result, startDate, endDate, guestId)

      val futureResponse = Await.result(Future(makeReservation(currentlyReservedClass)), timeout.duration)
      //        val response = Await.result(futureResponse, timeout.duration)
      println(s"Reservation ID: $futureResponse")
      //        val roomActor = context.actorOf(Props[RoomActor], s"room$result")
      //        roomActor ! RoomActor.Book(startDate, endDate)
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
    println("CANCELING RESERVATION.....")
    val cancellationResult = cancelReservation(reservationId)
    val cancelBookingResult = cancelBooking(reservationId)
    //  val bookingResult = addBooking(BookingClass(None, currentlyReserved.guestId, currentlyReserved.roomId, currentlyReserved.reservationStartDate, currentlyReserved.reservationEndDate))

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