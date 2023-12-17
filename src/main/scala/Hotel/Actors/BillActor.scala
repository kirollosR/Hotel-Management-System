package Hotel.Actors


import Hotel.Actors.BillActor.{CheckIn, CheckOut}
import Hotel.CRUDs.BillCRUD.{addBill, getBillAmountByBookingId, getBookingIdByGuestIdAndEndDate, getBookingIdByGuestIdAndStartDate}
import Hotel.CRUDs.GuestCRUD.{getGuestById, getGuestIdByName, updateGuest}
import Hotel.CRUDs.CurrentlyReservedCRUD.{cancelReservation, findAvailableRoom, getReservationIdByBookingId}
import Hotel.Models.{BillClass, GuestClass}
import akka.actor.{Actor, Props}
import akka.util.Timeout
import slick.ast.Library.Not

import java.time.format.DateTimeFormatter
import scala.concurrent.duration.DurationInt
import scala.io.StdIn
import scala.util.control.Exception.nonFatalCatch
import scala.concurrent.duration._
import java.time.LocalDate
import scala.concurrent.Await


object BillActor {
  case class CheckIn()
  case class CheckOut()

}

class BillActor extends Actor {
  import GuestActor._
  implicit val timeout: Timeout = Timeout(5.seconds)
  override def receive: Receive = {
    case CheckIn() => {
      checkIn
    }

    case CheckOut() => {
      checkOut
    }
  }




  def checkIn(): Unit = {
    print("Enter guest name: ")
    val guestName = scala.io.StdIn.readLine()
    val guestId = Await.result(getGuestIdByName(guestName), timeout.duration).get.get
    val guest = Await.result(getGuestById(guestId), timeout.duration).get
    val updatedGuest = GuestClass(guest.id, guest.name, true, guest.email, guest.phone)
    Await.result(updateGuest(updatedGuest), timeout.duration)
    print("Enter Start date: ")
    val startDate:LocalDate = readLocalDate()
    val bookingId = Await.result(getBookingIdByGuestIdAndStartDate(guestId, startDate), timeout.duration).get.get
    val billAmount = Await.result(getBillAmountByBookingId(bookingId), timeout.duration).get
    val bill = BillClass(None, bookingId, billAmount, LocalDate.now(), guestId)
    val billId = Await.result(addBill(bill), timeout.duration)
    println("Checked in successfully ... Enjoy your stay")

  }

  def checkOut(): Unit = {
    print("Enter guest name: ")
    val guestName = scala.io.StdIn.readLine()
    val guestId = Await.result(getGuestIdByName(guestName), timeout.duration).get.get
    val guest = Await.result(getGuestById(guestId), timeout.duration).get

    print("Enter End date: ")
    val endDate: LocalDate = readLocalDate()
    val bookingId = Await.result(getBookingIdByGuestIdAndEndDate(guestId, endDate), timeout.duration).get.get
    val removeCurrentReservation = Await.result(cancelReservation(bookingId), timeout.duration)
    val updatedGuest = GuestClass(guest.id, guest.name, false, guest.email, guest.phone)
    Await.result(updateGuest(updatedGuest), timeout.duration)
    println(s"Your Total Bill is: ${Await.result(getBillAmountByBookingId(guestId), timeout.duration).get}")
    println("Checked out successfully ... Hope you enjoyed your stay")
  }

  def readLocalDate(): LocalDate = {
    val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val userInput = StdIn.readLine()
    // Parse the user input into a LocalDate
    LocalDate.parse(userInput, dateFormat)
  }

}

