package me.rohank05;

import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;

public class PagesData {
    public final ArrayList<MessageEmbed> embeds;
    public Integer page = 0;
    public final Long userId;

    public PagesData(ArrayList<MessageEmbed> embeds, Long userId){
        this.embeds = embeds;
        this.userId = userId;
    }
    public PagesData(ArrayList<MessageEmbed> embeds){
        this.embeds = embeds;
        this.userId = null;
    }
    public void increasePage() {
        page++;
    }

    public void decreasePage() {
        page++;
    }
}
