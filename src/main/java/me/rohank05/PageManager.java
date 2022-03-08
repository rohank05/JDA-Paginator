package me.rohank05;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PageManager {
    private static PageManager INSTANCE;
    private final Map<Long, PagesData> pagesData;
    public PageManager(){
        this.pagesData = new HashMap<>();
    }
    public static PageManager getINSTANCE() {
        if(INSTANCE == null){
            INSTANCE = new PageManager();
        }
        return INSTANCE;
    }
    public void paginate(Message message, ArrayList<MessageEmbed> embedArray, Long userId, Long timeout){
        addButton(message);
        this.pagesData.computeIfAbsent(message.getIdLong(), (messageId) -> new PagesData(embedArray, userId));
        try {
            Thread.sleep(timeout);
            this.pagesData.remove(message.getIdLong());
            message.editMessageComponents().queue();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void paginate(Message message, ArrayList<MessageEmbed> embedArray, Long timeout){
        addButton(message);
        this.pagesData.computeIfAbsent(message.getIdLong(), (messageId) -> new PagesData(embedArray));
        try {
            Thread.sleep(timeout);
            this.pagesData.remove(message.getIdLong());
            message.editMessageComponents(ActionRow.of()).queue();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void paginate(Message message, ArrayList<MessageEmbed> embedArray){
        addButton(message);
        this.pagesData.computeIfAbsent(message.getIdLong(), (messageId) -> new PagesData(embedArray));
    }
    private void addButton(Message message){
        message.editMessageComponents(ActionRow.of(
                Button.primary("backward", "backward").withEmoji(Emoji.fromMarkdown("⬅️")).asDisabled(),
                Button.primary("forward", "forward").withEmoji(Emoji.fromMarkdown("➡️"))
        )).queue();
    }

    public void initListener(@NotNull ButtonInteractionEvent event){
        event.deferReply(true).queue();
        Long messageId = event.getMessageIdLong();
        if(!this.pagesData.containsKey(messageId)) return;
        PagesData pagesData = this.pagesData.get(messageId);
        if(pagesData.userId != null){
            if(!pagesData.userId.equals(event.getUser().getIdLong())) return;
        }
        if(event.getButton().getId().equals("forward")){
            MessageEmbed embed = pagesData.embeds.get(pagesData.page+1);
            MessageBuilder messageBuilder = new MessageBuilder().setEmbeds(embed);
            if(pagesData.page == 0){
                messageBuilder.setActionRows(ActionRow.of(Button.primary("backward", "backward").withEmoji(Emoji.fromMarkdown("⬅️")),
                        Button.primary("forward", "forward").withEmoji(Emoji.fromMarkdown("➡️"))));
            }
            if(pagesData.page == (pagesData.embeds.size() - 2)){
                messageBuilder.setActionRows(ActionRow.of(
                        Button.primary("backward", "backward").withEmoji(Emoji.fromMarkdown("⬅️")),
                        Button.primary("forward", "forward").withEmoji(Emoji.fromMarkdown("➡️")).asDisabled()
                ));
            }
            event.editMessage(messageBuilder.build()).queue();
            pagesData.increasePage();
        }
        else if(event.getButton().getId().equals("backward")){
            MessageEmbed embed = pagesData.embeds.get(pagesData.page-1);
            MessageBuilder messageBuilder = new MessageBuilder().setEmbeds(embed);
            if(pagesData.page == 1){
                messageBuilder.setActionRows(ActionRow.of(
                        Button.primary("backward", "backward").withEmoji(Emoji.fromMarkdown("⬅️")).asDisabled(),
                        Button.primary("forward", "forward").withEmoji(Emoji.fromMarkdown("➡️"))
                ));
            }
            if(pagesData.page == pagesData.embeds.size() -1){
                messageBuilder.setActionRows(ActionRow.of(
                        Button.primary("backward", "backward").withEmoji(Emoji.fromMarkdown("⬅️")),
                        Button.primary("forward", "forward").withEmoji(Emoji.fromMarkdown("➡️"))
                ));
            }
            event.editMessage(messageBuilder.build()).queue();
            pagesData.decreasePage();
        }
    }
}
