package Hotel.Actors
import Hotel.PrivateExecutor._
import Hotel.Models.Room.RoomTable
import Hotel.Models.{BookingClass, RoomClass}
import Hotel.connection.db
import akka.actor.{Actor, ActorRef, Props}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import slick.jdbc.MySQLProfile.api._

object BookingActor{

}


class BookingActor extends Actor {
  // Fetch rooms from the database and create RoomActors for each
  def getAllRoomActors: Map[Int, ActorRef] = {
    val roomsFuture: Future[Seq[RoomClass]] = db.run(RoomTable.result)

    val roomActorsFuture: Future[Map[Int, ActorRef]] = roomsFuture.map { rooms =>
      rooms.map { room =>
        room.id -> context.actorOf(Props[RoomActor], s"room${room.id}")
      }.toMap
    }

    Await.result(roomActorsFuture, Duration.Inf) // Blocking operation; consider handling asynchronously
  }

  def receive: Receive = {
    case request: BookingClass =>
      // Forward the booking request to the corresponding RoomActor
      getAllRoomActors.get(request.roomId).foreach(_ forward request)
  }
}

