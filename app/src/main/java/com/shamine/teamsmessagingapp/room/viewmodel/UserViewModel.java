package com.shamine.teamsmessagingapp.room.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.shamine.teamsmessagingapp.room.entities.User;
import com.shamine.teamsmessagingapp.room.repositories.UserRepository;

public class UserViewModel extends AndroidViewModel {
    private final LiveData<User> loggedInUserLiveData;

    public UserViewModel(@NonNull Application application) {
        super(application);
        UserRepository userRepository = new UserRepository(application);
        loggedInUserLiveData = userRepository.getLoggedInUserLiveData();
    }

    public LiveData<User> getLoggedInUserLiveData() {
        return loggedInUserLiveData;
    }
}
