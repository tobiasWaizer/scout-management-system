package com.scout.scoutmanagement.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "eventos_campamento")
@Getter
@Setter
public class EventoCampamento extends Evento {
    private String lugar;
    private Long contactoLugar;
}
