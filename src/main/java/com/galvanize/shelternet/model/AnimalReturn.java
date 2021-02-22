package com.galvanize.shelternet.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AnimalReturn {
    private List<AnimalReturnDto> animals;
}
