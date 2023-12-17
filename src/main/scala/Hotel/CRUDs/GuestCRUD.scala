package Hotel.CRUDs
import Hotel.Models.Guest.GuestTable
import Hotel.Models.{Guest, GuestClass}
import Hotel.PrivateExecutor._
import Hotel.connection.db

import scala.concurrent.{ExecutionContext, Future}




object GuestCRUD {

  import slick.jdbc.MySQLProfile.api._

// ------------------ SimpleCRUD ------------------
  def addGuest(guest: GuestClass): Future[Int] = {
    val result: Future[Option[Int]] = db.run(GuestTable returning  GuestTable.map(_.id) += guest)
    result.map(_.get)
  }

  def getGuestById(guestId: Int): Future[Option[GuestClass]] =
    db.run(GuestTable.filter(_.id === guestId).result.headOption)

  def getAllGuests(): Future[Seq[GuestClass]] =
    db.run(GuestTable.result)

  def updateGuest(guest: GuestClass): Future[Int] =
    db.run(GuestTable.filter(_.id === guest.id).update(guest))

  def deleteGuest(guestId: Int): Future[Int] =
    db.run(GuestTable.filter(_.id === guestId).delete)

  def getGuestIdByName(name: String): Future[Option[Option[Int]]] =
    db.run(GuestTable.filter(_.name === name).map(_.id).result.headOption)

  def findGuestsByName(substring: String): Future[Seq[GuestClass]] = {
    val query = GuestTable.filter(_.name like s"%$substring%").result
    db.run(query)
  }

  def findGuestIdByName(substring: String): Future[Seq[Option[Int]]] = {
    val query = GuestTable.filter(_.name like s"%$substring%").map(_.id).result
    db.run(query)
  }

  def findAllGuestsMails : Future[Seq[String]] = {
    val query = GuestTable.map(_.email).result
    db.run(query)
  }

  def findAllGuestsPhones : Future[Seq[String]] = {
    val query = GuestTable.map(_.phone).result
    db.run(query)
  }

  def countAvailableGuests(): Future[Int] =
    db.run(GuestTable.filter(_.status === true).length.result)

  def countGuests(): Future[Int] =
    db.run(GuestTable.length.result)
}