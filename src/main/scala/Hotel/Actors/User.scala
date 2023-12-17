package Hotel.Actors

import Hotel.Actors.BookingActor.{Book, _}
import Hotel.Actors.GuestActor.{AddGuest, AllGuests, DeleteGuest, UpdateGuest}
import Hotel.Actors.User.LiveTheLife
import Hotel.CRUDs.GuestCRUD.{addGuest, findGuestsByName, getAllGuests, getGuestById, getGuestIdByName}
import akka.actor.Actor
import Hotel.Main._
import Hotel.Models.{CurrentlyReservedClass, GuestClass}
import akka.util.Timeout

import java.time.format.DateTimeFormatter
import java.time.LocalDate
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import akka.pattern.ask

import scala.io.StdIn



object User {
  case class LiveTheLife()
}
class User extends Actor {
  implicit val timeout: Timeout = Timeout(5.seconds)

  var validGuestName = false
  val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  override def receive: Receive = {
    case LiveTheLife => {
      var choice = options()
      while (choice != 0) {
        choice match {
          case 1 => Booking()
          case 2 => cancelBooking()
          case 3 => updateGuest()
          case 4 => deleteGuest()
          case 5 => guestActor ! AllGuests()
          case 6 => bookingActor ! getUpcomingBookings()
          case _ => println("Invalid choice")
        }

        Thread.sleep(1000)
        choice = options()
      }

      if (choice == 0) {
        println("Exiting...")
        context.system.terminate()
        // Additional cleanup or termination logic
      }


    }
//            guestActor ! AddGuest(GuestClass(None, "Hassan", status = false, "Hassan@mail,com", "1516"))
      //      guestActor ! updateGuest(GuestClass(Some(12), "Ham", status = false, "Hammam@mail,com", "01255"))
      //      guestActor ! deleteGuest(12)

//      val futureResponse = (guestActor ? AddGuest(GuestClass(None, "Hassan", status = false, "Hassan@mail,com", "1516")))
//      val response = Await.result(futureResponse, timeout.duration)
//      println(response)
  }

  private def options(): Int = {
    println("Welcome to the Hotel Management System")
    println("Please select an option:")
    println("1. Booking")
    println("2. Cancel Booking")
    println("3. Update Guest")
    println("4. Delete Guest")
    println("5. Get All Guests")
    println("6. Get All Upcoming Bookings")
    println("0. Exit")
    print("Enter your choice: ")

    val choice = StdIn.readInt()
    choice
  }

  private def allGuests(): Unit = {
    guestActor ! AllGuests()

  }

  private def deleteGuest(): Unit = {
    print("Enter guest name: ")
    val guestName = StdIn.readLine()
    val guests = Await.result(findGuestsByName(guestName), timeout.duration)
    if (guests.nonEmpty) {
      guests.foreach {
        guest =>
          val id = guest.id.getOrElse(-1)
          println(s"Guest ID: ${id} | Name: ${guest.name} | Status: ${guest.status} | Email: ${guest.email} | Phone: ${guest.phone}")
      }
      print("Did you find the guest you were looking for? (y/n): ")
      val foundGuest = StdIn.readLine().toLowerCase == "y"
      if (foundGuest) {
        print("Enter guest ID: ")
        val guestId = StdIn.readInt()
        guestActor ! DeleteGuest(guestId)
      }
      validGuestName = true // Exit the loop when a valid name is entered
    } else {
      println("No guests found")
    }
  }

  private def updateGuest(): Unit = {
    print("Enter guest name: ")
    val guestName = StdIn.readLine()
    val guests = Await.result(findGuestsByName(guestName), timeout.duration)
    if (guests.nonEmpty) {
      guests.foreach {
        guest =>
          val id = guest.id.getOrElse(-1)
          println(s"Guest ID: ${id} | Name: ${guest.name} | Status: ${guest.status} | Email: ${guest.email} | Phone: ${guest.phone}")
      }
      print("Did you find the guest you were looking for? (y/n): ")
      val foundGuest = StdIn.readLine().toLowerCase == "y"
      if (foundGuest) {
        print("Enter guest ID: ")
        val guestId = StdIn.readInt()
        val guest = Await.result(getGuestById(guestId), timeout.duration)
        guest match {
          case Some(guest) =>
            println(s"Guest ID: ${guest.id.asInstanceOf[Int]} | Name: ${guest.name} | Status: ${guest.status} | Email: ${guest.email} | Phone: ${guest.phone}")
            print("Enter new name: ")
            val newName = StdIn.readLine()
            print("Enter new email: ")
            val newEmail = StdIn.readLine()
            print("Enter new phone: ")
            val newPhone = StdIn.readLine()

            guestActor ! UpdateGuest(GuestClass(Some(guestId), newName, guest.status, newEmail, newPhone))
          case None =>
            println("No guest found.")
        }
      }
      validGuestName = true // Exit the loop when a valid name is entered
    } else {
      println("No guests found")
    }
  }

  private def cancelBooking(): Unit = {
    print("Enter booking ID: ")
    val reservationId = StdIn.readInt()
    bookingActor ! Cancel(reservationId)
  }

  private def Booking(): Unit = {
    while (!validGuestName) {
      print("Enter guest name (type 'exit' to quit): ")
      val guestName = StdIn.readLine()

      if (guestName.toLowerCase == "exit") {
        println("Exiting...")
        context.system.terminate()
      } else {
        if (guestName.nonEmpty) {
          val guests = Await.result(findGuestsByName(guestName), timeout.duration)
          if (guests.nonEmpty) {
            guests.foreach {
              guest =>
                val id = guest.id.getOrElse(-1)
                println(s"Guest ID: ${id} | Name: ${guest.name} | Status: ${guest.status} | Phone: ${guest.phone}")
            }
            print("Did you find the guest you were looking for? (y/n): ")
            val foundGuest = StdIn.readLine().toLowerCase == "y"
            if (foundGuest) {
              print("Enter guest ID: ")
              val guestId = StdIn.readInt()
              createBooking(guestId)
              validGuestName = true // Exit the loop when a valid name is entered
            } else {
              val guestId = addNewGuest(guestName)
              createBooking(guestId)
              validGuestName = true // Exit the loop when a valid name is entered
            }
          } else {
            val guestId = addNewGuest(guestName)
            createBooking(guestId)
            validGuestName = true
          }
        }
      }
    }
  }

  private def createBooking(guestId: Int): Unit = {
    print("Enter start date (yyyy-MM-dd): ")
    val startDate = readLocalDate()
    print("Enter end date (yyyy-MM-dd): ")
    val endDate = readLocalDate()
    print("Enter room capacity: ")
    val roomCapacity = StdIn.readInt()

    bookingActor ! Book(roomCapacity, startDate, endDate, guestId)
  }

  private def addNewGuest(guestName: String): Int = {
    println("Add new Guest")
//    print("Enter guest name: ")
//    val guestName = StdIn.readLine()
    print("Enter guest email: ")
    val guestEmail = StdIn.readLine()
    print("Enter guest phone: ")
    val guestPhone = StdIn.readLine()

    val futureResponse = (guestActor ? AddGuest(GuestClass(None, guestName, status = false, guestEmail, guestPhone)))
    val guestId = Await.result(futureResponse, timeout.duration)
    guestId.asInstanceOf[Int]
  }

  def readLocalDate(): LocalDate = {
    val userInput = StdIn.readLine()
    // Parse the user input into a LocalDate
    LocalDate.parse(userInput, dateFormat)
  }
}
