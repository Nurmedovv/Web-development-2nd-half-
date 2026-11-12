package by.bsu.lab4.entity;

import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(BodyType.class)
public abstract class BodyType_ {
    public static volatile SingularAttribute<BodyType, Integer> id;
    public static volatile SingularAttribute<BodyType, String> name;
    public static volatile ListAttribute<BodyType, Car> cars;   // обратная связь
}
