package com.bonnesbaby.challenge.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosAuthor(
        @JsonAlias("name") String name,
        @JsonAlias("birth_year") String birth_year,
        @JsonAlias("death_year") String death_year



) {}
