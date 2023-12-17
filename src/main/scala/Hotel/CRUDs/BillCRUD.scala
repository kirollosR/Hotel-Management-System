package Hotel.CRUDs

import Hotel.Models.{BillClass, BookingClass, RoomClass}
import Hotel.Models.Bill.BillTable
import Hotel.Models.Booking.BookingTable
import Hotel.PrivateExecutor._
import Hotel.connection.db

import scala.concurrent.Future
import slick.jdbc.MySQLProfile.api._

import java.time.LocalDate
import java.time.temporal.ChronoUnit
object BillCRUD {

  // ------------------ SimpleCRUD ------------------
  def addBill(bill: BillClass): Future[Int] = {
    val result: Future[Option[Int]] = db.run(BillTable returning BillTable.map(_.id) += bill)
    result.map(_.get)
  }

  def deleteBill(billId: Int): Future[Int] =
    db.run(BillTable.filter(_.id === billId).delete)

  def getBillByBookingId(bookingId: Int): Future[Option[BillClass]] =
    db.run(BillTable.filter(_.bookingId === bookingId).result.headOption)

  def getBookingIdByGuestIdAndStartDate(guestId: Int, startDate: LocalDate): Future[Option[Option[Int]]] =
    db.run(BookingTable.filter(booking => booking.guestId === guestId && booking.reservationStartDate === startDate).map(_.id).result.headOption)

  def getBookingIdByGuestIdAndEndDate(guestId: Int, endDate: LocalDate): Future[Option[Option[Int]]] =
    db.run(BookingTable.filter(booking => booking.guestId === guestId && booking.reservationEndDate === endDate).map(_.id).result.headOption)

  def checkStartDate(guestId: Int, startDate: LocalDate): Future[Boolean] =
    db.run(BookingTable.filter(booking => booking.guestId === guestId && booking.reservationStartDate === startDate).map(_.reservationStartDate).exists.result)

  def checkEndDate(guestId: Int, endDate: LocalDate): Future[Boolean] =
    db.run(BookingTable.filter(booking => booking.guestId === guestId && booking.reservationEndDate === endDate).map(_.reservationEndDate).exists.result)

  //------------------ ComplexCRUD to CALCULATE Amount ------------------
  def getBillAmountByBookingId(bookingId: Int): Future[Option[Double]] = {
    val bookingFuture: Future[Option[BookingClass]] = BookingCRUD.getBookingById(bookingId)

    bookingFuture.flatMap {
      case Some(booking) =>
        // Assuming you have a method to get the room details by room ID
        val roomFuture: Future[Option[RoomClass]] = RoomCRUD.getRoomById(booking.roomId)

        roomFuture.map {
          case Some(room) =>
            val reservationDurationInDays: Int = calculateReservationDuration(booking.reservationStartDate, booking.reservationEndDate)
            val billAmount: Double = reservationDurationInDays * room.price
            Some(billAmount)
          case None => None // Room not found
        }
      case None => Future.successful(None) // Booking not found
    }
  }

  private def calculateReservationDuration(startDate: LocalDate, endDate: LocalDate): Int = {
    // Calculate the difference between two LocalDates in days
    val duration: Long = ChronoUnit.DAYS.between(startDate, endDate)
    duration.toInt
  }
}