package Hotel.Models

import slick.jdbc.GetResult

import java.time.LocalDate

case class BookingClass(
       id: Option[Int],
       roomId: Int,
       reservationStartDate: LocalDate,
       reservationEndDate: LocalDate,
       guestId: Int,
)
object Booking {
  import slick.jdbc.MySQLProfile.api._

  class BookingTable(tag: Tag) extends Table[BookingClass](tag, "booking") {
    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def guestId = column[Int]("guest_id")
    def roomId = column[Int]("room_id")
    def reservationStartDate = column[LocalDate]("reservation_start_date")
    def reservationEndDate = column[LocalDate]("reservation_end_date")

    // mapping function to transform from tuple to domain object and vice versa
    override def * = (id, roomId, reservationStartDate, reservationEndDate, guestId) <> (BookingClass.tupled, BookingClass.unapply)
  }
  lazy val BookingTable = TableQuery[BookingTable]

  implicit val getBookingResult = GetResult(r => BookingClass(r.nextIntOption(), r.nextInt, r.nextDate.toLocalDate, r.nextDate.toLocalDate, r.nextInt))

}
