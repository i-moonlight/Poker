package be.kdg.mobile_client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import be.kdg.mobile_client.R;
import be.kdg.mobile_client.adapters.FriendRecyclerAdapter;
import be.kdg.mobile_client.model.User;
import be.kdg.mobile_client.shared.CallbackWrapper;
import be.kdg.mobile_client.services.SharedPrefService;
import be.kdg.mobile_client.services.UserService;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity for displaying friends of the current user.
 */
public class FriendsActivity extends BaseActivity {
    @BindView(R.id.lvFriends) RecyclerView lvFriends;
    @BindView(R.id.btnSearch) Button btnSearch;
    @BindView(R.id.btnBack) Button btnBack;
    @Inject SharedPrefService sharedPrefService;
    @Inject UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getControllerComponent().inject(this);
        checkIfAuthorized(sharedPrefService);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        ButterKnife.bind(this);
        addEventListners();
        getFriends();
    }

    private void addEventListners() {
        btnSearch.setOnClickListener(e -> {
            Intent intent = new Intent(this, UserSearchActivity.class);
            startActivity(intent);
        });
        btnBack.setOnClickListener(e -> {
            Intent intent = new Intent(this,  MenuActivity.class);
            startActivity(intent);
        });
    }

    private void getFriends() {
        userService.getMySelf().enqueue(new CallbackWrapper<>((throwable, response) -> {
            if (response.isSuccessful() && response.body() != null) {
                initializeAdapter(response.body().getFriends());
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.error_message), Toast.LENGTH_LONG).show();
            }
        }));
    }

    /**
     * Initializes the friend adapter to show all the friends that
     * were retrieved from the user-service back-end.
     *
     * @param friends The friends that need to be used by the adapter.
     */
    private void initializeAdapter(List<User> friends) {
        FriendRecyclerAdapter friendAdapter = new FriendRecyclerAdapter(friends);
        lvFriends.setAdapter(friendAdapter);
        lvFriends.setLayoutManager(new LinearLayoutManager(this));
    }
}
