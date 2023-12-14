package Hotel.Models

import slick.jdbc.GetResult

case class RoomClass(
  id: Int,
  capacity: Int,
  price: Double
)
object Room {
  import slick.jdbc.MySQLProfile.api._
  class RoomTable(tag: Tag) extends Table[RoomClass](tag, "room") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def capacity = column[Int]("capacity")
    def price = column[Double]("price")

    // mapping function to transform from tuple to domain object and vice versa
    override def * = (id, capacity, price) <> (RoomClass.tupled, RoomClass.unapply)
  }
  lazy val RoomTable = TableQuery[RoomTable]

  implicit val getRoomResult = GetResult(r => RoomClass(r.nextInt, r.nextInt, r.nextDouble))

}
