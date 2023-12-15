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


object GuestDao {

  import slick.jdbc.MySQLProfile.api._
  import PrivateExecutor._

// ------------------ SimpleCRUD ------------------
  def addGuest(guest: GuestClass): Future[Int] = {
    db.run(GuestTable returning  GuestTable.map(_.id) += guest)
  }

  def getGuestById(guestId: Int): Future[Option[GuestClass]] =
    db.run(GuestTable.filter(_.id === guestId).result.headOption)

  def getAllGuests: Future[Seq[GuestClass]] =
    db.run(GuestTable.result)

  def updateGuest(guest: GuestClass): Future[Int] =
    db.run(GuestTable.filter(_.id === guest.id).update(guest))

  def deleteGuest(guestId: Int): Future[Int] =
    db.run(GuestTable.filter(_.id === guestId).delete)


//// ------------------ CRUD WITH ERROR HANDLING ------------------
//
//
//    // Create
//    def addGuest(guest: GuestClass): Future[Int] =
//      db.run((GuestTable returning GuestTable.map(_.id)) += guest)
//        .map { guestId =>
//          println(s"Guest added successfully with ID: $guestId")
//          guestId
//        }
//        .recover {
//          case ex: Exception =>
//            println(s"Error adding guest: ${ex.getMessage}")
//            throw ex
//        }
//
//    // Read
//    def getGuestById(guestId: Int): Future[Option[GuestClass]] =
//      db.run(GuestTable.filter(_.id === guestId).result.headOption)
//        .recover {
//          case ex: Exception =>
//            println(s"Error retrieving guest: ${ex.getMessage}")
//            throw ex
//        }
//
//    def getAllGuests: Future[Seq[GuestClass]] =
//      db.run(GuestTable.result)
//        .recover {
//          case ex: Exception =>
//            println(s"Error retrieving guests: ${ex.getMessage}")
//            throw ex
//        }
//
//    // Update
//    def updateGuest(guest: GuestClass): Future[Int] =
//      db.run(GuestTable.filter(_.id === guest.id).update(guest))
//        .map { _ =>
//          println("Guest updated successfully.")
//          1 // or you can return any meaningful value
//        }
//        .recover {
//          case ex: Exception =>
//            println(s"Error updating guest: ${ex.getMessage}")
//            throw ex
//        }
//
//    // Delete
//    def deleteGuest(guestId: Int): Future[Int] =
//      db.run(GuestTable.filter(_.id === guestId).delete)
//        .map { _ =>
//          println("Guest deleted successfully.")
//          1 // or you can return any meaningful value
//        }
//        .recover {
//          case ex: Exception =>
//            println(s"Error deleting guest: ${ex.getMessage}")
//            throw ex
//        }
//}