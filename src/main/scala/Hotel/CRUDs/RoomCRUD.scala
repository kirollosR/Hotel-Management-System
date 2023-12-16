package Hotel.CRUDs
import Hotel.Models.Guest.GuestTable
import Hotel.Models.Room.RoomTable
import Hotel.Models.{Guest, GuestClass, RoomClass}
import Hotel.PrivateExecutor._
import Hotel.connection.db

import java.time.LocalDate
import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}



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

//  def addGuest(guest: GuestClass): Future[Int] = {
//    val result: Future[Option[Int]] = db.run(GuestTable returning GuestTable.map(_.id) += guest)
//    result.map(_.get)
//  }
//
//  def getGuestById(guestId: Int): Future[Option[GuestClass]] =
//    db.run(GuestTable.filter(_.id === guestId).result.headOption)
//
//  def getAllGuests: Future[Seq[GuestClass]] =
//    db.run(GuestTable.result)
//
//  def updateGuest(guest: GuestClass): Future[Int] =
//    db.run(GuestTable.filter(_.id === guest.id).update(guest))
//
//  def deleteGuest(guestId: Int): Future[Int] =
//    db.run(GuestTable.filter(_.id === guestId).delete)

}