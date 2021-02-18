package com.galvanize.shelternet.model;

import lombok.Value;

@Value
public class AnimalTransfer {
    Long shelterIdFrom;
    Long shelterIdTo;
    Long animalId;
}
