package by.bsu.lab4.entity;

import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.LocalDate;

@StaticMetamodel(Request.class)
public abstract class Request_ {
    public static volatile SingularAttribute<Request, Integer> id;
    public static volatile SingularAttribute<Request, Customer> customer;
    public static volatile SingularAttribute<Request, Route> route;
    public static volatile SingularAttribute<Request, BodyType> requiredBodyType;
    public static volatile SingularAttribute<Request, Integer> requiredLoadKg;
    public static volatile SingularAttribute<Request, LocalDate> departureDate;
    public static volatile ListAttribute<Request, Trip> trips;   // обратная связь
}
