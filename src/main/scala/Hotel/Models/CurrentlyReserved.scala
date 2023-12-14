package Hotel.Models
import java.time.LocalTime

case class CurrentlyReservedClass(
                                   id: Int,
                                   roomId: Int,
                                   reservationStartDate: LocalTime,
                                   reservationEndDate: LocalTime
                                  )
object CurrentlyReserved {
  import slick.jdbc.MySQLProfile.api._

  class CurrentlyReservedTable(tag: Tag) extends Table[CurrentlyReservedClass](tag, "currently_reserved") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def roomId = column[Int]("room_id")
    def reservationStartDate = column[LocalTime]("reservation_start_date")
    def reservationEndDate = column[LocalTime]("reservation_end_date")

    // mapping function to transform from tuple to domain object and vice versa
    override def * = (id, roomId, reservationStartDate, reservationEndDate) <> (CurrentlyReservedClass.tupled, CurrentlyReservedClass.unapply)
  }
  lazy val CurrentlyReservedTable = TableQuery[CurrentlyReservedTable]

}
