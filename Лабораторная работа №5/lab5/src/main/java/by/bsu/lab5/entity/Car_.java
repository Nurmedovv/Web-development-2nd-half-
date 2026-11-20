package by.bsu.lab5.entity;

import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Car.class)
public abstract class Car_ {
    public static volatile SingularAttribute<Car, Integer> id;
    public static volatile SingularAttribute<Car, String> plateNumber;
    public static volatile SingularAttribute<Car, String> model;
    public static volatile SingularAttribute<Car, BodyType> bodyType;
    public static volatile SingularAttribute<Car, Integer> loadKg;
    public static volatile SingularAttribute<Car, String> status;
    public static volatile ListAttribute<Car, Trip> trips;
}
