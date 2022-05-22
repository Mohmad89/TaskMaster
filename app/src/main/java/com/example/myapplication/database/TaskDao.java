package com.example.myapplication.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myapplication.data.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insertAll(Task task);

    @Query("SELECT * FROM task")
    List <Task> getAllTasks();

    @Query("SELECT * FROM task WHERE id = :id")
    Task getTask(long id);
}
