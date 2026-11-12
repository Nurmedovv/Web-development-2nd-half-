package by.bsu.lab4.entity;

import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Driver.class)
public abstract class Driver_ {
    public static volatile SingularAttribute<Driver, Integer> id;
    public static volatile SingularAttribute<Driver, String> fullName;
    public static volatile SingularAttribute<Driver, String> licenseCategory;
    public static volatile SingularAttribute<Driver, String> phone;
    public static volatile ListAttribute<Driver, Trip> trips;   // обратная связь
}
