import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Room} from '../model/room';
import {Act} from '../model/act';
import {Player} from '../model/player';

/**
 * This service is used to manage all the HTTP traffic of the
 * game micro-service.
 */
@Injectable({
  providedIn: 'root'
})
export class GameService {
  // url = 'https://poker-game-service.herokuapp.com/api/rooms';
  url = 'http://localhost:5001/api/rooms';

  constructor(private http: HttpClient) {
  }

  /**
   * Gives back all the rooms that are in the
   * database of the micro-service.
   */
  getRooms(): Observable<Room[]> {
    return this.http.get<Room[]>(this.url);
  }

  /**
   * Gives back a single room based on the id of the room.
   *
   * @param id The id of the room that needs to be returned.
   */
  getRoom(id: number): Observable<Room> {
    return this.http.get<Room>(this.url + '/' + id);
  }

  /**
   * Lets a user join into a room.
   *
   * @param id The id of the room that the player wants to join.
   */
  joinRoom(id: number): Observable<Player> {
    return this.http.post<Player>(this.url + '/' + id + '/join-room', '');
  }

  /**
   * Lets a player leave from the current room.
   *
   * @param id The id of the room that the player wants to leave.
   */
  leaveRoom(id: number): Observable<Player> {
    return this.http.delete<Player>(this.url + '/' + id + '/leave-room');
  }

  changeRoom(room: Room): Observable<Room> {
    console.log(room);
    return this.http.put<Room>(this.url + "/" + room.id, room);
  }

  getPlayer(): Observable<Player> {
    return this.http.get<Player>(this.url + '/players');
  }

  changePlayer(player: Player): Observable<Player> {
    return this.http.put<Player>(this.url + '/players', player);
  }

  /**
   * Saves an act that was played by the player to the backend.
   *
   * @param act The act that needs to be saved.
   */
  addAct(act: Act): Observable<Act> {
    return this.http.post<Act>(this.url, act);
  }
}
