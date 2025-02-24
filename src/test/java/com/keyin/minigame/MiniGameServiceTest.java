package com.keyin.minigame;

import com.keyin.ui.GameInterfaceGUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MiniGameServiceTest {

    private MiniGameService miniGameService;

    @Mock
    private GameInterfaceGUI mockGui;

    @Mock
    private AbstractMiniGame mockMiniGame;

    @BeforeEach
    void setUp() {
        miniGameService = new MiniGameService();
    }

    @Test
    void startMiniGame_ShouldStartGameAndSetCallbacks() {
        Long locationId = 1L;
        Long heroId = 2L;
        Runnable onComplete = mock(Runnable.class);

        mockStatic(MiniGameFactory.class);
        when(MiniGameFactory.createMiniGame(locationId, heroId, mockGui)).thenReturn(mockMiniGame);

        miniGameService.startMiniGame(locationId, heroId, mockGui, onComplete);

        verify(mockMiniGame).setOnCompleteCallback(onComplete);
        verify(mockMiniGame).setOnFailCallback(any(Runnable.class));
        verify(mockMiniGame).startGame();
    }
}