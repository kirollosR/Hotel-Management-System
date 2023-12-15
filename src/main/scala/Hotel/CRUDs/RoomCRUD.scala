package Hotel.CRUDs
import Hotel.Models.Guest.GuestTable
import Hotel.Models.{Guest, GuestClass}
import Hotel.connection
import Hotel.connection.db

import java.time.LocalDate
import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object PrivateExecutor {
  val executor = Executors.newFixedThreadPool(4)
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executor)
}


object RoomCRUDDao {

  import slick.jdbc.MySQLProfile.api._
  import PrivateExecutor._

  // ------------------ SimpleCRUD ------------------
  def addGuest(guest: GuestClass): Future[Int] = {
    val result: Future[Option[Int]] = db.run(GuestTable returning  GuestTable.map(_.id) += guest)
    result.map(_.get)
  }

  def getGuestById(guestId: Int): Future[Option[GuestClass]] =
    db.run(GuestTable.filter(_.id === guestId).result.headOption)

  def getAllGuests: Future[Seq[GuestClass]] =
    db.run(GuestTable.result)

  def updateGuest(guest: GuestClass): Future[Int] =
    db.run(GuestTable.filter(_.id === guest.id).update(guest))

  def deleteGuest(guestId: Int): Future[Int] =
    db.run(GuestTable.filter(_.id === guestId).delete)