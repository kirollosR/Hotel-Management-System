package Hotel.CRUDs

import Hotel.Models.Room.RoomTable
import Hotel.Models.{Guest, GuestClass, RoomClass}

import Hotel.connection.db


import scala.concurrent.{ExecutionContext, Future}




object RoomCRUD {

  import slick.jdbc.MySQLProfile.api._

  // ------------------ SimpleCRUD ------------------
  def addRoom(room: RoomClass): Future[Int] = {
    val result: Future[Int] = db.run(RoomTable returning RoomTable.map(_.id) += room)
    result
  }

  def getRoomById(roomId: Int): Future[Option[RoomClass]] =
    db.run(RoomTable.filter(_.id === roomId).result.headOption)

  def getAllRoomsByCapacity(capacity: Int): Future[Seq[RoomClass]] =
    db.run(RoomTable.filter(_.capacity === capacity).result)

  def countRooms(): Future[Int] =
    db.run(RoomTable.length.result)


}