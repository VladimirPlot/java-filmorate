package ru.yandex.practicum.filmorate.storage.friendship;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFriendshipStorage implements FriendshipStorage {

    private final Map<Integer, Map<Integer, FriendshipStatus>> friendships = new HashMap<>();

    @Override
    public void addFriend(int userId, int friendId) {
        friendships
                .computeIfAbsent(userId, k -> new HashMap<>())
                .put(friendId, FriendshipStatus.PENDING);

        Map<Integer, FriendshipStatus> friendMap = friendships.getOrDefault(friendId, new HashMap<>());
        if (friendMap.get(userId) == FriendshipStatus.PENDING) {
            friendships.get(userId).put(friendId, FriendshipStatus.CONFIRMED);
            friendMap.put(userId, FriendshipStatus.CONFIRMED);
        }
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        Map<Integer, FriendshipStatus> userFriends = friendships.get(userId);
        Map<Integer, FriendshipStatus> friendFriends = friendships.get(friendId);

        if (userFriends != null) {
            userFriends.remove(friendId);
        }

        if (friendFriends != null) {
            FriendshipStatus status = friendFriends.get(userId);
            if (status == FriendshipStatus.CONFIRMED) {
                friendFriends.put(userId, FriendshipStatus.PENDING);
            } else {
                friendFriends.remove(userId);
            }
        }
    }

    @Override
    public Set<Integer> getFriends(int userId) {
        return friendships.getOrDefault(userId, Collections.emptyMap()).entrySet().stream()
                .filter(entry -> entry.getValue() == FriendshipStatus.CONFIRMED)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Integer> getCommonFriends(int userId, int otherUserId) {
        Set<Integer> userFriends = getFriends(userId);
        Set<Integer> otherUserFriends = getFriends(otherUserId);

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Integer> getPendingRequestsFor(int userId) {
        Set<Integer> pendingRequesters = new HashSet<>();

        for (Map.Entry<Integer, Map<Integer, FriendshipStatus>> entry : friendships.entrySet()) {
            int requesterId = entry.getKey();
            Map<Integer, FriendshipStatus> relations = entry.getValue();

            FriendshipStatus statusToUser = relations.get(userId);
            if (statusToUser == FriendshipStatus.PENDING) {
                Map<Integer, FriendshipStatus> userRelations = friendships.getOrDefault(userId, Collections.emptyMap());
                if (userRelations.get(requesterId) != FriendshipStatus.CONFIRMED) {
                    pendingRequesters.add(requesterId);
                }
            }
        }

        return pendingRequesters;
    }
}