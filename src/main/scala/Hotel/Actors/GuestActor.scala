package Hotel.Actors

import Hotel.CRUDs.GuestCRUD.{addGuest, deleteGuest, updateGuest}
import Hotel.Models.GuestClass
import akka.actor.{Actor, Props}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object GuestActor {
  // Define your GuestClass here or import it from your existing code
  case class Guest(id: Int, name: String, status: Boolean, email: String, phone: String)
  // Messages for CRUD operations
  case class AddGuest(guest: GuestClass)
  case class GetGuestById(id: Int)
  case class UpdateGuest(guest: GuestClass)
  case class DeleteGuest(id: Int)

  def props: Props = Props[GuestActor]
}

class GuestActor extends Actor {
  import GuestActor._

  override def receive: Receive = {
    case AddGuest(guest) =>
      println("Adding guest ")
      val result = Await.result(addGuest(guest), 2.seconds)
      sender() ! result
//      println(s"Guest ${guest.name} added successfully with ID ${guest.id}")

    case UpdateGuest(guest) =>
      println("Updating guest ")
      updateGuest(guest)
      println(s"Guest ${guest.name} updated successfully")

    case DeleteGuest(id) => {
      println("Deleting guest ")
      deleteGuest(id)
      println(s"Guest with ID $id deleted successfully")
    }
  }
}
