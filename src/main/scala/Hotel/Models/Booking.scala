package Hotel.Models

import java.time.LocalTime

case class BookingClass(
                         id: Int,
                         guestId: Int,
                         roomId: Int,
                         reservationStartDate: LocalTime,
                         reservationEndDate: LocalTime,
)
object Booking {
  import slick.jdbc.MySQLProfile.api._

  class BookingTable(tag: Tag) extends Table[BookingClass](tag, "booking") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def guestId = column[Int]("guest_id")
    def roomId = column[Int]("room_id")
    def reservationStartDate = column[LocalTime]("check_in_date")
    def reservationEndDate = column[LocalTime]("check_out_date")

    // mapping function to transform from tuple to domain object and vice versa
    override def * = (id, guestId, roomId, reservationStartDate, reservationEndDate) <> (BookingClass.tupled, BookingClass.unapply)
  }
  lazy val BookingTable = TableQuery[BookingTable]

}
