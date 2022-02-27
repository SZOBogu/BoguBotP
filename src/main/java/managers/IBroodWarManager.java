package managers;

import pojos.TextInGame;

import java.util.List;

public interface IBroodWarManager {
    void manage();
    List<TextInGame> getTextToWriteInGame();
}
