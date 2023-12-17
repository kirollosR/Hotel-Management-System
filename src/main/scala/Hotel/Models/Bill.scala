package Hotel.Models

import slick.jdbc.GetResult

import java.time.LocalDate

case class BillClass(
      id: Option[Int],
      bookingId: Int,
      amount: Double,
      issuedDate: LocalDate,
      guestId: Int
)

object Bill {
  import slick.jdbc.MySQLProfile.api._

  class BillTable(tag: Tag) extends Table[BillClass](tag, "bill") {
    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def bookingId = column[Int]("booking_id")
    def amount = column[Double]("amount")
    def issuedDate = column[LocalDate]("issued_date")
    def guestId = column[Int]("guest_id")

    // mapping function to transform from tuple to domain object and vice versa
    override def * = (id, bookingId, amount, issuedDate, guestId) <> (BillClass.tupled, BillClass.unapply)
  }
  lazy val BillTable = TableQuery[BillTable]

  implicit val getBillResult = GetResult(r => BillClass(r.nextIntOption, r.nextInt, r.nextDouble, r.nextDate.toLocalDate, r.nextInt))

}


