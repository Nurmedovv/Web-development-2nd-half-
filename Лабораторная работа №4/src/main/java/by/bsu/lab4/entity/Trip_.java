package by.bsu.lab4.entity;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.LocalDate;

@StaticMetamodel(Trip.class)
public abstract class Trip_ {
    public static volatile SingularAttribute<Trip, Integer> id;
    public static volatile SingularAttribute<Trip, Request> request;
    public static volatile SingularAttribute<Trip, Driver> driver;
    public static volatile SingularAttribute<Trip, Car> car;
    public static volatile SingularAttribute<Trip, String> status;
    public static volatile SingularAttribute<Trip, LocalDate> departureDate;
    public static volatile SingularAttribute<Trip, LocalDate> completionDate;
    public static volatile SingularAttribute<Trip, String> carConditionAfter;
}
