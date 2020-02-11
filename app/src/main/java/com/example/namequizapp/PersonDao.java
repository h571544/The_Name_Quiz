package com.example.namequizapp;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


@Dao
public interface PersonDao {

    @Insert
    public void insertPerson(Person person);

    @Query("SELECT * FROM persons WHERE nickname = :name")
    List<Person> findPerson(String name);

    @Query("SELECT * FROM persons WHERE nickname = :name")
    List<Person> findPersonId(String name);

    @Query("SELECT * FROM persons")
    public Person[] loadAllUsers();

    @Query("SELECT * FROM persons")
    public List<Person> loadAllPersons();

    @Query("DELETE FROM persons WHERE id = :id")
    void deletePerson(int id);

    @Query("SELECT * FROM persons")
    List<Person> getAllPersons();
}