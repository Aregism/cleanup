package com.cleanup.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DataContainer<T> {
    private List<T> data;
}
