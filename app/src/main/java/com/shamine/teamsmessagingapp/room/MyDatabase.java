package com.shamine.teamsmessagingapp.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.shamine.teamsmessagingapp.room.dao.BackupResourceDao;
import com.shamine.teamsmessagingapp.room.dao.ChatGroupDao;
import com.shamine.teamsmessagingapp.room.dao.ChatGroupMemberDao;
import com.shamine.teamsmessagingapp.room.dao.ContactDao;
import com.shamine.teamsmessagingapp.room.dao.GroupMessageDeliveryDao;
import com.shamine.teamsmessagingapp.room.dao.MessageGroupDao;
import com.shamine.teamsmessagingapp.room.dao.MessagePrivateDao;
import com.shamine.teamsmessagingapp.room.dao.UserDao;
import com.shamine.teamsmessagingapp.room.entities.BackupResource;
import com.shamine.teamsmessagingapp.room.entities.ChatGroup;
import com.shamine.teamsmessagingapp.room.entities.ChatGroupMember;
import com.shamine.teamsmessagingapp.room.entities.Contact;
import com.shamine.teamsmessagingapp.room.entities.GroupMessageDelivery;
import com.shamine.teamsmessagingapp.room.entities.MessageGroup;
import com.shamine.teamsmessagingapp.room.entities.MessagePrivate;
import com.shamine.teamsmessagingapp.room.entities.User;
import com.shamine.teamsmessagingapp.room.views.ChatGroupView;
import com.shamine.teamsmessagingapp.room.views.ContactGroupDbView;
import com.shamine.teamsmessagingapp.room.views.ContactView;

@Database(version = 3, exportSchema = false, entities = {BackupResource.class, ChatGroup.class, ChatGroupMember.class,
        MessageGroup.class, MessagePrivate.class, User.class, Contact.class,
        GroupMessageDelivery.class},
        views = {ContactGroupDbView.class, ContactView.class, ChatGroupView.class})
public abstract class MyDatabase extends RoomDatabase {
    private static MyDatabase instance;

    public static synchronized MyDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), MyDatabase.class, "TeamsMessaging")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract BackupResourceDao backupResourceDao();

    public abstract ChatGroupDao chatGroupDao();

    public abstract ChatGroupMemberDao chatGroupMemberDao();

    public abstract MessageGroupDao messageGroupDao();

    public abstract MessagePrivateDao messagePrivateDao();

    public abstract UserDao userDao();

    public abstract ContactDao contactDao();

    public abstract GroupMessageDeliveryDao groupMessageDeliveryDao();
}
