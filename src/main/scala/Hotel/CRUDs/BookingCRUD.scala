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

  def cancelBooking(bookingId: Int): Future[Int] = {
    val query = BookingTable.filter(_.id === bookingId).delete
    db.run(query)
  }

  def getAllFutureBookings(): Future[Seq[BookingClass]] = {
    val query = BookingTable.filter(_.reservationStartDate >= LocalDate.now()).result
    db.run(query)
  }

  def getUpcomingBooking(): Future[Seq[(BookingClass, GuestClass)]] = {
    val today = LocalDate.now()
    val query = for {
      (booking, guest) <- BookingTable.filter(_.reservationStartDate >= today) join GuestTable on (_.guestId === _.id)
    } yield (booking, guest)
    db.run(query.result)
  }

  def countAllBookings(): Future[Int] =
    db.run(BookingTable.length.result)
}
