package Hotel.Actors

import Hotel.Actors.BookingActor.{Book, _}
import Hotel.Actors.GuestActor.AddGuest
import Hotel.Actors.User.LiveTheLife
import Hotel.CRUDs.GuestCRUD.{addGuest, findGuestsByName, getAllGuests, getGuestIdByName}
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

  //  override def receive: Receive = {
  //    case LiveTheLife => {
  //      val guestName = "sayed"
  //      val GuestId = Await.result(getGuestIdByName(guestName), timeout.duration).get.get
  //      println(GuestId)
  //      bookingActor ! Book(2, LocalDate.of(2023, 11, 20), LocalDate.of(2023, 11, 25), GuestId)
  ////      bookingActor ! Book(2, LocalDate.of(2023, 11, 28), LocalDate.of(2023, 11, 30), GuestId)
  //      bookingActor ! Cancel(3)
  //    }
  //  }
  var validGuestName = false
  val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  override def receive: Receive = {
    case LiveTheLife => {
      var choice = options()
      while(choice != 0) {
        choice match {
          case 1 => Booking()
          case 2 => cancelBooking()
          case 3 => println("Add Guest")
          case 4 => println("Update Guest")
          case 5 => println("Delete Guest")
          case 6 => println("Get All Guests")
          case 7 => bookingActor ! getUpcomingBookings()
          case _ => println("Invalid choice")
        }
        Thread.sleep(1000)
        choice = options()
      }
      if(choice == 0) {
        println("Exiting...")
        context.system.terminate()
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
    println("3. Add Guest")
    println("4. Update Guest")
    println("5. Delete Guest")
    println("6. Get All Guests")
    println("7. Get All Upcoming Bookings")
    println("0. Exit")
    print("Enter your choice: ")
    val choice = StdIn.readInt()
    choice
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
        val allGuests = Await.result(getAllGuests, timeout.duration)

        if (guestName.nonEmpty && allGuests.exists(_.name == guestName)) {
          val guests = Await.result(findGuestsByName(guestName), timeout.duration)
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
            print("Enter start date (yyyy-MM-dd): ")
            val startDate = readLocalDate()
            print("Enter end date (yyyy-MM-dd): ")
            val endDate = readLocalDate()
            print("Enter room capacity: ")
            val roomCapacity = StdIn.readInt()

            bookingActor ! Book(roomCapacity, startDate, endDate, guestId)

            validGuestName = true // Exit the loop when a valid name is entered
          } else {
            println("Add new Guest")
            // add new guest function

            print("Enter guest ID: ")
            val guestId = StdIn.readInt()
            print("Enter start date (yyyy-MM-dd): ")
            val startDate = readLocalDate()
            print("Enter end date (yyyy-MM-dd): ")
            val endDate = readLocalDate()
            print("Enter room capacity: ")
            val roomCapacity = StdIn.readInt()

            bookingActor ! Book(roomCapacity, startDate, endDate, guestId)

            validGuestName = true // Exit the loop when a valid name is entered
          }

        } else {
          println("Invalid guest name. Please try again.")
        }
      }
    }
  }

  def readLocalDate(): LocalDate = {
    val userInput = StdIn.readLine()
    // Parse the user input into a LocalDate
    LocalDate.parse(userInput, dateFormat)
  }
}
