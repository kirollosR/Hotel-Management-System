package Hotel.Models
import slick.jdbc.GetResult

import java.time.LocalDate

case class CurrentlyReservedClass(
                                   id: Int,
                                   roomId: Int,
                                   reservationStartDate: LocalDate,
                                   reservationEndDate: LocalDate
                                  )
object CurrentlyReserved {
  import slick.jdbc.MySQLProfile.api._

  class CurrentlyReservedTable(tag: Tag) extends Table[CurrentlyReservedClass](tag, "currently_reserved") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def roomId = column[Int]("room_id")
    def reservationStartDate = column[LocalDate]("reservation_start_date")
    def reservationEndDate = column[LocalDate]("reservation_end_date")

    // mapping function to transform from tuple to domain object and vice versa
    override def * = (id, roomId, reservationStartDate, reservationEndDate) <> (CurrentlyReservedClass.tupled, CurrentlyReservedClass.unapply)
  }
  lazy val CurrentlyReservedTable = TableQuery[CurrentlyReservedTable]

  implicit val getCurrentlyReservedResult = GetResult(r => CurrentlyReservedClass(r.nextInt, r.nextInt, r.nextDate.toLocalDate, r.nextDate.toLocalDate))

}
