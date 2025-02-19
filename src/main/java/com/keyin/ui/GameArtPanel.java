package com.keyin.ui;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;

public class GameArtPanel extends JPanel {
    private final JTextArea artArea;

    public GameArtPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Create text area for ASCII art
        artArea = new JTextArea();
        artArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        artArea.setBackground(Color.BLACK);
        artArea.setForeground(Color.WHITE);
        artArea.setEditable(false);

        String plushieArt =
            "         .m.                                   ,_\n" +
                    "         ' ;M;                                ,;m `\n" +
                    "           ;M;.           ,      ,           ;SMM;\n" +
                    "          ;;Mm;         ,;  ____  ;,         ;SMM;\n" +
                    "         ;;;MM;        ; (.MMMMMM.) ;       ,SSMM;;\n" +
                    "       ,;;;mMp'        l  ';mmmm;/  j       SSSMM;;\n" +
                    "     .;;;;;MM;         .\\,.mmSSSm,,/,      ,SSSMM;;;\n" +
                    "    ;;;;;;mMM;        .;MMmSSSSSSSmMm;     ;MSSMM;;;;\n" +
                    "   ;;;;;;mMSM;     ,_ ;MMmS;;;;;;mmmM;  -,;MMMMMMm;;;;\n" +
                    "  ;;;;;;;MMSMM;     \\\"*;M;( ( '') );m;*\"/ ;MMMMMM;;;;;,\n" +
                    " .;;;;;;mMMSMM;      \\(@;! _     _ !;@)/ ;MMMMMMMM;;;;;,\n" +
                    " ;;;;;;;MMSSSM;       ;,;.*o*> <*o*.;m; ;MMMMMMMMM;;;;;;,\n" +
                    ".;;;;;;;MMSSSMM;     ;Mm;           ;M;,MMMMMMMMMMm;;;;;;.\n" +
                    ";;;;;;;mmMSSSMMMM,   ;Mm;,   '-    ,;M;MMMMMMMSMMMMm;;;;;;;\n" +
                    ";;;;;;;MMMSSSMMMMMMMm;Mm;;,  ___  ,;SmM;MMMMMMSSMMMM;;;;;;;;\n" +
                    ";;'\";;;MMMSSSSMMMMMM;MMmS;;,  \"  ,;SmMM;MMMMMMSSMMMM;;;;;;;;.\n" +
                    "!   ;;;MMMSSSSSMMMMM;MMMmSS;;._.;;SSmMM;MMMMMMSSMMMM;;;;;;;;;\n" +
                    "    ;;;;*MSSSSSSMMMP;Mm*\"'q;'   `;p*\"*M;MMMMMSSSSMMM;;;;;;;;;\n" +
                    "    ';;;  ;SS*SSM*M;M;'     `-.        ;;MMMMSSSSSMM;;;;;;;;;,\n" +
                    "     ;;;. ;P  `q; qMM.                 ';MMMMSSSSSMp' ';;;;;;;\n" +
                    "     ;;;; ',    ; .mm!     \\.   `.   /  ;MMM' `qSS'    ';;;;;;\n" +
                    "     ';;;       ' mmS';     ;     ,  `. ;'M'   `S       ';;;;;\n" +
                    "      `;;.        mS;;`;    ;     ;    ;M,!     '  luk   ';;;;\n" +
                    "       ';;       .mS;;, ;   '. o  ;   oMM;                ;;;;\n" +
                    "        ';;      MMmS;; `,   ;._.' -_.'MM;                 ;;;\n" +
                    "         `;;     MMmS;;; ;   ;      ;  MM;                 ;;;\n" +
                    "           `'.   'MMmS;; `;) ',    .' ,M;'                 ;;;\n" +
                    "              \\    '' ''; ;   ;    ;  ;'                   ;;\n" +
                    "               ;        ; `,  ;    ;  ;                   ;;\n" +
                    "                        |. ;  ; (. ;  ;      _.-.         ;;\n" +
                    "           .-----..__  /   ;  ;   ;' ;\\  _.-\" .- `.      ;;\n" +
                    "         ;' ___      `*;   `; ';  ;  ; ;'  .-'    :      ;\n" +
                    "         ;     \"\"\"*-.   `.  ;  ;  ;  ; ' ,'      /       |\n" +
                    "         ',          `-_    (.--',`--'..'      .'        ',\n" +
                    "           `-_          `*-._'.\\\\\\;||\\\\)     ,'\n" +
                    "              `\"*-._        \"*`-ll_ll'l    ,'\n" +
                    "                 ,==;*-._           \"-.  .'\n" +
                    "              _-'    \"*-=`*;-._        ;'\n" +
                    "            .\"            ;'  ;\"*-.    `\n" +
                    "            ;   ____      ;//'     \"-   `,\n" +
                    "            `+   .-/                 \".\\\\;\n" +
                    "              `*\" /                    \"'";

    artArea.setText(plushieArt);

    // Center the art
    JPanel centeringPanel = new JPanel(new GridBagLayout());
    centeringPanel.setBackground(Color.BLACK);
    centeringPanel.add(artArea);

    add(centeringPanel, BorderLayout.CENTER);
}

// Method to update the art if needed
public void setArt(String newArt) {
    artArea.setText(newArt);
}
}