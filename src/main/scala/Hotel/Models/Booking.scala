package Hotel.Models

import slick.jdbc.GetResult

import java.time.LocalDate

case class BookingClass(
                         id: Int,
                         guestId: Int,
                         roomId: Int,
                         reservationStartDate: LocalDate,
                         reservationEndDate: LocalDate,
)
object Booking {
  import slick.jdbc.MySQLProfile.api._

  class BookingTable(tag: Tag) extends Table[BookingClass](tag, "booking") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def guestId = column[Int]("guest_id")
    def roomId = column[Int]("room_id")
    def reservationStartDate = column[LocalDate]("check_in_date")
    def reservationEndDate = column[LocalDate]("check_out_date")

    // mapping function to transform from tuple to domain object and vice versa
    override def * = (id, guestId, roomId, reservationStartDate, reservationEndDate) <> (BookingClass.tupled, BookingClass.unapply)
  }
  lazy val BookingTable = TableQuery[BookingTable]

  implicit val getBookingResult = GetResult(r => BookingClass(r.nextInt, r.nextInt, r.nextInt, r.nextDate.toLocalDate, r.nextDate.toLocalDate))

}
