package bgu.spl.net.messages;

import java.util.LinkedList;

public class PostMessage implements Message{
    private final short opcode = 5;
    private String content;

    @Override
    public short getOpcode() {return opcode;}

    public String getContent() {return content;}

    public void setContent(String content) {this.content = content;}

    public LinkedList<String> getMentions(){
        LinkedList<String> mentions = new LinkedList<>();
        for(int i=0; i<content.length(); i++){//go through content until finding '@'
            //If found one then continue going through content until reaching space, and add chars along the way to a string:
            if (content.charAt(i) == '@'){
                i = i + 1;
                String mention = "";
                while (i<content.length() && content.charAt(i) != ' '){
                    mention = mention + content.charAt(i);
                    i++;
                }
                mentions.add(mention);
            }
        }
        return mentions;
    }
}
