package io.alakazam.testing.app;

import io.alakazam.testing.Person;

public interface PeopleStore {
    Person fetchPerson(String name);
}
