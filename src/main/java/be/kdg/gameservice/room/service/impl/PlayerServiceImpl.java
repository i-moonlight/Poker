package be.kdg.gameservice.room.service.impl;

import be.kdg.gameservice.room.exception.RoomException;
import be.kdg.gameservice.room.model.Player;
import be.kdg.gameservice.room.model.Room;
import be.kdg.gameservice.room.persistence.PlayerRepository;
import be.kdg.gameservice.room.service.api.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {
    private final PlayerRepository playerRepository;
    private final RoomServiceImpl roomService;

    /**
     * Adds a player to a room.
     *
     * @param roomId The id of the room the player needs to be added to
     * @param userId The id of the user.
     * @return The newly created player.
     * @throws RoomException Thrown if the maximum capacity for the room is reached.
     * @see Player for extra information about player constructor
     * @see Room for extra information about helper methods.
     */
    @Override
    public Player joinRoom(int roomId, String userId) throws RoomException {
        //Get room
        Room room = roomService.getRoom(roomId);

        //Determine if room is full
        if (room.getPlayersInRoom().size() > room.getGameRules().getMaxPlayerCount())
            throw new RoomException(RoomServiceImpl.class, "Maximum player capacity is reached.");

        //Add player to room
        Player player = new Player(room.getGameRules().getStartingChips(), userId, room.getFirstEmptySeat());
        player = playerRepository.save(player);
        room.addPlayer(player);
        roomService.saveRoom(room);
        return player;
    }

    /**
     * Removes the player form the room and the current round.
     *
     * @param roomId The id of the room were the players needs to be removed from
     * @param userId The id of the player we want to delete.
     * @throws RoomException Thrown if the player was not found in the room.
     */
    @Override
    public Player leaveRoom(int roomId, String userId) throws RoomException {
        //Get data
        Room room = roomService.getRoom(roomId);
        Optional<Player> playerOpt = room.getPlayersInRoom().stream()
                .filter(player -> player.getUserId().equals(userId))
                .findAny();

        //Check optional player
        if (!playerOpt.isPresent())
            throw new RoomException(RoomServiceImpl.class, "Player was not in the room.");

        //Removes player from current round
        if (room.getRounds().size() > 0) {
            if (!room.getCurrentRound().isFinished() && room.getCurrentRound().getPlayersInRound().contains(playerOpt.get())) {
                room.getCurrentRound().removePlayer(playerOpt.get());
            }
        }

        //Remove player from the room
        room.removePlayer(playerOpt.get());
        roomService.saveRoom(room);
        playerRepository.delete(playerOpt.get());
        return playerOpt.get();
    }

    @Override
    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }

    @Override
    public Player getPlayer(String userId) {
        return playerRepository.getByUserId(userId);
    }
}
