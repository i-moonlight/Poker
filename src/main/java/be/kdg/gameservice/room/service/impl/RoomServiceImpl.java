package be.kdg.gameservice.room.service.impl;

import be.kdg.gameservice.room.exception.RoomException;
import be.kdg.gameservice.room.model.GameRules;
import be.kdg.gameservice.room.model.Player;
import be.kdg.gameservice.room.model.Room;
import be.kdg.gameservice.room.persistence.RoomRepository;
import be.kdg.gameservice.room.service.api.RoomService;
import be.kdg.gameservice.round.model.Round;
import be.kdg.gameservice.round.service.api.RoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * This service will be used to manage the ongoing activity of a specific room.
 * It will also take care of the CRUD operations with its persistence dependency.
 */
@Transactional
@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final RoundService roundService;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository, RoundService roundService) {
        this.roomRepository = roomRepository;
        this.roundService = roundService;
    }

    /**
     * WARING!!!!!!!!!!!!!!! remove method.
     */
    private void addDefaultPlayers() throws RoomException {
        savePlayer(1, "Jarne");
        savePlayer(1, "Bryan");
        savePlayer(1, "Remi");
        savePlayer(1, "Anthony");
        savePlayer(1, "Maarten");
    }

    /**
     * Adds a player to a room.
     *
     * @param roomId The id of the room the player needs to be added to
     * @param name   The name of the player
     * @return The newly created player.
     * @throws RoomException Thrown if the maximum capacity for the room is reached.
     * @see Player for extra information about player constructor
     * @see Room for extra information about helper methods.
     */
    @Override
    public Player savePlayer(int roomId, String name) throws RoomException {
        //Get room
        Room room = getRoom(roomId);

        //Determine if room is full
        if (room.getPlayersInRoom().size() > room.getGameRules().getMaxPlayerCount())
            throw new RoomException(RoomServiceImpl.class, "Maximum player capacity is reached.");

        //Add player to room
        Player player = new Player(room.getGameRules().getStartingChips(), name);
        room.addPlayer(player);
        saveRoom(room);
        return player;
    }

    /**
     * @param roomId The room the new round needs to be created for.
     * @return The updated room with the new round.
     * @throws RoomException Thrown if there are less than 2 players in the round.
     */
    @Override
    public Room startNewRoundForRoom(int roomId) throws RoomException {
        //Get room
        Room room = getRoom(roomId);

        //Determine if round can be created
        List<Player> players = room.getPlayersInRoom();
        if (players.size() < 2)
            throw new RoomException(RoomServiceImpl.class, "There must be at least 2 players int room to start a round.");
        int button = room.getRounds().size() == 0 ? 0 : room.getCurrentRound().getButton();

        //Create new round
        Round round = roundService.startNewRound(room.getPlayersInRoom(), button);
        if (room.getRounds().size() > 0) room.getCurrentRound().setFinished(true);
        room.addRound(round);
        return saveRoom(room);
    }

    /**
     * @param roomId The id of the room.
     * @return The corresponding room.
     * @throws RoomException Thrown if the room does not exists in the database.
     */
    private Room getRoom(int roomId) throws RoomException {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomException(RoomServiceImpl.class, "The room was not found in the database."));
    }

    /**
     * @param room The room that needs to be updated or saved.
     */
    private Room saveRoom(Room room) {
        return roomRepository.save(room);
    }
}