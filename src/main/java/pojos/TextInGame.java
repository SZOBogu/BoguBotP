package pojos;

import bwapi.Text;

public class TextInGame {
    private String text;
    private int x;
    private int y;
    private Text color;

    public TextInGame(TextInGameBuilder builder) {
        this.text = builder.text;
        this.x = builder.x;
        this.y = builder.y;
        this.color = builder.color;
    }

    public static class TextInGameBuilder{
        private String text;
        private int x;
        private int y;
        private Text color;

        public TextInGameBuilder(String text){
            this.text = text;
            this.x = 0;
            this.y = 0;
            this.color = Text.White;
        }

        public TextInGameBuilder x(int x){
            this.x = x;
            return this;
        }

        public TextInGameBuilder y(int y){
            this.y = y;
            return this;
        }

        public TextInGameBuilder color(Text color){
            this.color = color;
            return this;
        }

        public TextInGame build(){
            return new TextInGame(this);
        }
    }

    public String getText() {
        return text;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Text getColor() {
        return color;
    }
}
