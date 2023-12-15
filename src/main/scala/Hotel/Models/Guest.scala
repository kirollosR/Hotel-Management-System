package Hotel.Models
import slick.jdbc.GetResult

case class GuestClass(
  id: Option[Int],
  name: String,
  status: Boolean,
  email: String,
  phone: String
)
object Guest {
  import slick.jdbc.MySQLProfile.api._

  class GuestTable(tag: Tag) extends Table[GuestClass](tag, "guest") {
    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def status = column[Boolean]("status")
    def email = column[String]("email")
    def phone = column[String]("phone")

    // mapping function to transform from tuple to domain object and vice versa
    override def * = (id, name, status, email, phone) <> (GuestClass.tupled, GuestClass.unapply)
  }
  lazy val GuestTable = TableQuery[GuestTable]

  implicit val getGuestResult = GetResult(r => GuestClass(r.nextIntOption(), r.nextString, r.nextBoolean, r.nextString, r.nextString))

}
