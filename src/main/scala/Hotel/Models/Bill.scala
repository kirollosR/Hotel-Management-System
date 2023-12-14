package Hotel.Models

import java.time.LocalDate

case class BillClass(
                      id: Int,
                      bookingId: Int,
                      amount: Double,
                      issuedDate: LocalDate
)
class Bill {
  object Bill {
    import slick.jdbc.MySQLProfile.api._

    class BillTable(tag: Tag) extends Table[Bill](tag, "bill") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def bookingId = column[Int]("booking_id")
      def amount = column[Double]("amount")
      def issuedDate = column[LocalDate]("issued_date")

      // mapping function to transform from tuple to domain object and vice versa
      override def * = (id, bookingId, amount, issuedDate) <> (BillClass.tupled, BillClass.unapply)
    }
    lazy val BillTable = TableQuery[BillTable]

  }

}
