package com.lycoon.clashbot.utils.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user")
public class User
{
    public User(long id) { this.id = id; }

    @Id
    @Column(name = "id")
    @Getter @Setter
    private Long id;

    @Column(name = "lang")
    @Getter @Setter
    private String lang;

    @Column(name = "player_tag")
    @Getter @Setter
    private String playerTag;

    @Column(name = "clan_tag")
    @Getter @Setter
    private String clanTag;

    public User withLang(String lang) { this.lang = lang; return this; }
    public User withPlayerTag(String playerTag) { this.playerTag = playerTag; return this; }
    public User withClanTag(String clanTag) { this.clanTag = clanTag; return this; }
}
