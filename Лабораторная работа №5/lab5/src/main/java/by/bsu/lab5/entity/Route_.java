package by.bsu.lab5.entity;

import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Route.class)
public abstract class Route_ {
    public static volatile SingularAttribute<Route, Integer> id;
    public static volatile SingularAttribute<Route, String> cityFrom;
    public static volatile SingularAttribute<Route, String> cityTo;
    public static volatile ListAttribute<Route, Request> requests;
}
