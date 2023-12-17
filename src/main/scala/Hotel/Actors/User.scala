package Hotel.Actors

import Hotel.Actors.BillActor.{CheckIn, CheckOut}
import Hotel.Actors.BookingActor.{Book, _}
import Hotel.Actors.GuestActor.{AddGuest, AllGuests, DeleteGuest, UpdateGuest}
import Hotel.Actors.ReportActor.{CountAllBookings, CountAvaialableGuests, CountGuests, CountRooms, CountUpcomingBookings}
import Hotel.Actors.User.LiveTheLife
import Hotel.CRUDs.BillCRUD.{addBill, checkEndDate, checkStartDate, getBillAmountByBookingId, getBookingIdByGuestIdAndEndDate, getBookingIdByGuestIdAndStartDate}
import Hotel.CRUDs.CurrentlyReservedCRUD.cancelReservation
import Hotel.CRUDs.GuestCRUD
import Hotel.CRUDs.GuestCRUD.{findGuestsByName, getGuestById, updateGuest}
import akka.actor.Actor
import Hotel.Main._
import Hotel.Models.{BillClass, GuestClass}
import akka.util.Timeout

import java.time.format.DateTimeFormatter
import java.time.LocalDate
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.{Duration, DurationInt}
import akka.pattern.ask

import scala.io.StdIn



object User {
  case class LiveTheLife()
}
class User extends Actor {
  implicit val timeout: Timeout = Timeout(5.seconds)

  private var validGuestName = false
  val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
  override def receive: Receive = {
    case LiveTheLife =>
      var choice = options()
      while (choice != 0) {
        choice match {
          case 1 => Booking()
          case 2 => cancelBooking()
          case 3 => updateGuest()
          case 4 => deleteGuest()
          case 5 => guestActor ! AllGuests()
          case 6 => bookingActor ! getUpcomingBookings()
          case 7 => report()
          case 8 => checkIn()
          case 9 => checkOut()
          case _ => println("Invalid choice")
        }

        Thread.sleep(1000)
        choice = options()
      }

      if (choice == 0) {
        println("Exiting...")
        context.system.terminate()
      }

  }

  private def options(): Int = {
    println("------------------------------------------------------")
    println("Welcome to the Hotel Management System")
    println("Please select an option:")
    println("1. Booking")
    println("2. Cancel Booking")
    println("3. Update Guest")
    println("4. Delete Guest")
    println("5. Get All Guests")
    println("6. Get All Upcoming Bookings")
    println("7. Report")
    println("8. Check In")
    println("9. Check Out")
    println("0. Exit")
    print("Enter your choice: ")

    val choice = StdIn.readInt()
    choice
  }

  private def report(): Unit = {
    println("Report:")
    print("\t 1. Number of Rooms: ")
    val futureResponse = (reportActor ? CountRooms())
    val result = Await.result(futureResponse, timeout.duration)
    print(result + "\n")

    print("\t 2. Number of Guests: ")
    val futureResponse2 = (reportActor ? CountGuests())
    val result2 = Await.result(futureResponse2, timeout.duration)
    print(result2 + "\n")

    print("\t 3. Number of guests in the hotel now: ")
    val futureResponse3 = (reportActor ? CountAvaialableGuests())
    val result3 = Await.result(futureResponse3, timeout.duration)
    print(result3 + "\n")

    print("\t 4. Number of upcoming bookings: ")
    val futureResponse4 = (reportActor ? CountUpcomingBookings())
    val result4 = Await.result(futureResponse4, timeout.duration)
    print(result4 + "\n")

    print("\t 5. Number of all bookings: ")
    val futureResponse5 = (reportActor ? CountAllBookings())
    val result5 = Await.result(futureResponse5, timeout.duration)
    print(result5 + "\n")


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
        return
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
    validGuestName = false
  }

  private def createBooking(guestId: Int): Unit = {
    print("Enter start date (dd-MM-yyyy): ")
    val startDate = readLocalDate()
    print("Enter end date (dd-MM-yyyy): ")
    val endDate = readLocalDate()
    print("Enter room capacity: ")
    val roomCapacity = StdIn.readInt()

    bookingActor ! Book(roomCapacity, startDate, endDate, guestId)
  }

  private def addNewGuest(guestName: String): Int = {
    println("Add new Guest")
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
    LocalDate.parse(userInput, dateFormat)
  }



















  private def checkIn() = {
    print("Enter guest name: ")
    val guestName = StdIn.readLine()
    val guests = Await.result(findGuestsByName(guestName), timeout.duration)
    var guestId = -1
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
        guestId = StdIn.readInt()
      } else {
        println("No guests found")
      }
    } else {
      println("No guests found")
    }
    val guest = Await.result(getGuestById(guestId), timeout.duration).get
    val updatedGuest = GuestClass(guest.id, guest.name, true, guest.email, guest.phone)
    Await.result(GuestCRUD.updateGuest(updatedGuest), timeout.duration)
    print("Enter Start date: ")
    val startDate: LocalDate = readLocalDate1()
    if (Await.result(checkStartDate(guestId, startDate), timeout.duration)) {
      val bookingId = Await.result(getBookingIdByGuestIdAndStartDate(guestId, startDate), timeout.duration).get.get
      val billAmount = Await.result(getBillAmountByBookingId(bookingId), timeout.duration).get
      val bill = BillClass(None, bookingId, billAmount, LocalDate.now(), guestId)
      val billId = Await.result(addBill(bill), timeout.duration)
      println("Checked in successfully ... Enjoy your stay")
    } else {
      println("You have no reservation on this date")
    }
  }

  private def checkOut() = {
    print("Enter guest name: ")
    val guestName = StdIn.readLine()
    val guests = Await.result(findGuestsByName(guestName), timeout.duration)
    var guestId = -1
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
        guestId = StdIn.readInt()
      } else {
        println("No guests found")
      }
    } else {
      println("No guests found")
    }
    val guest = Await.result(getGuestById(guestId), timeout.duration).get

    print("Enter End date: ")
    val endDate: LocalDate = readLocalDate1()
    if (Await.result(checkEndDate(guestId, endDate), timeout.duration)) {
      val bookingId = Await.result(getBookingIdByGuestIdAndEndDate(guestId, endDate), timeout.duration).get.get
      val removeCurrentReservation = Await.result(cancelReservation(bookingId), timeout.duration)
      val updatedGuest = GuestClass(guest.id, guest.name, false, guest.email, guest.phone)
      Await.result(GuestCRUD.updateGuest(updatedGuest), timeout.duration)
      val test = Await.result(getBillAmountByBookingId(bookingId), timeout.duration)
      println(s"Your Total Bill is: ${test.get}")
      println("Checked out successfully ... Hope you enjoyed your stay")
    } else {
      println("You have no reservation on this date")
    }
  }

  def readLocalDate1(): LocalDate = {
    val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val userInput = StdIn.readLine()
    // Parse the user input into a LocalDate
    LocalDate.parse(userInput, dateFormat)
  }
}
