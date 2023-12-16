package Hotel.Models
import slick.jdbc.GetResult

import java.time.LocalDate

case class CurrentlyReservedClass(
                                   id: Option[Int],
                                   roomId: Int,
                                   reservationStartDate: LocalDate,
                                   reservationEndDate: LocalDate,
                                   guestId: Int
                                  )
object CurrentlyReserved {
  import slick.jdbc.MySQLProfile.api._

  class CurrentlyReservedTable(tag: Tag) extends Table[CurrentlyReservedClass](tag, "currently_reserved") {
    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def roomId = column[Int]("room_id")
    def reservationStartDate = column[LocalDate]("reservation_start_date")
    def reservationEndDate = column[LocalDate]("reservation_end_date")
    def guestId = column[Int]("guest_id")

    // mapping function to transform from tuple to domain object and vice versa
    override def * = (id, roomId, reservationStartDate, reservationEndDate, guestId) <> (CurrentlyReservedClass.tupled, CurrentlyReservedClass.unapply)
  }
  lazy val CurrentlyReservedTable = TableQuery[CurrentlyReservedTable]

  implicit val getCurrentlyReservedResult = GetResult(r => CurrentlyReservedClass(r.nextIntOption(), r.nextInt, r.nextDate.toLocalDate, r.nextDate.toLocalDate,  r.nextInt))

}
