package Hotel.Actors

import Hotel.CRUDs.GuestCRUD
import Hotel.CRUDs.GuestCRUD.{addGuest, deleteGuest, findAllGuestsMails, findAllGuestsPhones, getAllGuests, updateGuest}

import Hotel.Models.GuestClass

import akka.actor.{Actor, Props}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt

object GuestActor {
  // Define your GuestClass here or import it from your existing code
  case class Guest(id: Int, name: String, status: Boolean, email: String, phone: String)
  // Messages for CRUD operations
  case class AddGuest(guest: GuestClass)
  case class AllGuests()
  case class UpdateGuest(guest: GuestClass)
  case class DeleteGuest(id: Int)

  def props: Props = Props[GuestActor]
}

class GuestActor extends Actor {

  import GuestActor._

  override def receive: Receive = {
    case AddGuest(guest) =>
      if (isGuestExistsByEmailOrPhone(guest)){
        val result = Await.result(addGuest(guest), 2.seconds)
        sender() ! result
        println(s"Guest ${guest.name} added successfully with ID ${guest.id}")
      }


    case UpdateGuest(guest) =>
      if (!GuestIdExists(guest.id.get))
        println(s"Guest with ID ${guest.id} doesn't exist")
      else {
        val result = Await.result(updateGuest(guest), 2.seconds)
        sender() ! result
        println(s"Guest ${guest.name} updated successfully")
      }


    case DeleteGuest(id) =>
      if (!GuestIdExists(id))
        println(s"Guest with ID $id doesn't exist")
      else {
        val result = Await.result(deleteGuest(id), 2.seconds)
        sender() ! result
        println(s"Guest with ID $id deleted successfully")
      }

    case AllGuests() =>
      val result = Await.result(getAllGuests(), 2.seconds)
      result.foreach {
        case (guest) =>
          println(s"Guest ID: ${guest.id.getOrElse("N/A")}, Name: ${guest.name}, Status: ${guest.status}, Email: ${guest.email}, Phone: ${guest.phone}")
      }
//      println(s"Guests: $result")
  }


  def isGuestExistsByEmailOrPhone(newGuest: GuestClass): Boolean = {
    val guestsMails = Await.result(findAllGuestsMails, 2.seconds)
    val guestsPhones = Await.result(findAllGuestsPhones, 2.seconds)
    if (guestsMails.contains(newGuest.email)) {
      println("Email already exists")
      false
    } else if (guestsPhones.contains(newGuest.phone)) {
      println("Phone already exists")
      false
    } else {
      true
    }
  }

  def GuestIdExists(id: Int): Boolean = {
    val guest = Await.result(GuestCRUD.getGuestById(id), 2.seconds)
    if (guest.isEmpty) {
      false
    } else {
      true
    }
  }

}
