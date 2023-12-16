package Hotel.CRUDs

import Hotel.Models.Booking.BookingTable
import Hotel.Models.CurrentlyReserved._
import Hotel.Models.Guest.GuestTable
import Hotel.Models.{BookingClass, CurrentlyReservedClass, Guest, GuestClass, RoomClass}
import Hotel.PrivateExecutor._
import Hotel.connection.db

import java.time.LocalDate
import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object BookingCRUD {
  import slick.jdbc.MySQLProfile.api._

  def addBooking(booking: BookingClass): Future[Int] = {
    val result: Future[Option[Int]] = db.run(BookingTable returning BookingTable.map(_.id) += booking)
    result.map(_.get)
  }

  def getAllBookings(): Future[Seq[BookingClass]] = {
    val query = BookingTable.result
    db.run(query)
  }

//  def isRoomReserved(roomId: Int, startDate: LocalDate, endDate: LocalDate): Future[Boolean] = {
//    val slickQuery = CurrentlyReservedTable
//      .filter(reservation =>
//        reservation.roomId === roomId &&
//          reservation.reservationStartDate <= endDate &&
//          reservation.reservationEndDate >= startDate
//      )
//      .exists
//      .result
//
//    db.run(slickQuery)
//  }

//  def findAvailableRoom(rooms: Seq[RoomClass], startDate: LocalDate, endDate: LocalDate): Future[Either[Boolean, Int]] = {
//    // Create a Future[Boolean] for each room and collect them into a Seq[Future[Boolean]]
//    val roomChecks: Seq[Future[Boolean]] = rooms.map(room => isRoomReserved(room.id, startDate, endDate))
//
//    // Use Future.sequence to transform Seq[Future[Boolean]] into Future[Seq[Boolean]]
//    val allRoomChecks: Future[Seq[Boolean]] = Future.sequence(roomChecks)
//
//    // Use map to process the result when all the room checks are completed
//    allRoomChecks.map { results =>
//      // Find the index of the first false result
//      results.indexOf(false) match {
//        case index if index != -1 => Right(rooms(index).id) // Room is available, return its id
//        case _ => Left(false) // All rooms are reserved
//      }
//    }
//  }



}
