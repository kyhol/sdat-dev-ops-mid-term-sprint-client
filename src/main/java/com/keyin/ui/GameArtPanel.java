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

        String asciiArt =
                "       .-\"\"-.                     .-\"\"-.          \n" +
                        "     .'_.-.  |                   |  .-._'.        \n" +
                        "    /    _/ /       _______       \\ \\_    \\       \n" +
                        "   /.--.' | |      `=======`      | | '.--.\\\n" +
                        "  /   .-`-| |       _.---._       | |-`-.   \\\n" +
                        " ;.--':   | |     .'     / '.     | |   :'--.;\n" +
                        "|    _\\.'-| |    /      _/   \\    | |-'.\\_    |\n" +
                        ";_.-'/:\\   | |   /   .-'`  '.  \\   | |   :\\'-._;   \n" +
                        "|   | _:-'\\  \\.'   | ^ _ ^ |   './  /'-:_ |   |    \n" +
                        ";  .:` '._ \\.'     \\   _   /     \\ / _.' `:.  ;     \n" +
                        "|-` '-.;_ ./        '.___.'       '.` _;.-' `-|     \n" +
                        "; / .'\\ | |        _.'   '._        \\'| /'. \\ ;    \n" +
                        "| .' / `'.|      .' `\"---\"` '.       |'` \\ '. |    \n" +
                        ";/  /\\_/-`\\      |           |       /-\\_/\\  \\;    \n" +
                        " |.' .| `; \\     | |       | |      /;` |. '.|     \n" +
                        " |  / \\.'\\ /\\    | :--\"\"\"--: |    /`_/'./ \\  |    \n" +
                        "  \\| ; | ; |/)   | |       | |   (\\| : | ; |/      \n" +
                        "   \\ | ; | //    \\.'-.....-'./    \\\\ | ; | /       \n" +
                        "    `\\ | |`/    .' / ;|: | \\ '.    |`| | /`        \n" +
                        "jgs  .-:_/ \\_.-' .' / ' . : '. '.  /`\\_:'          \n" +
                        "     |  \\```      .'  ;     \\    `:                \n" +
                        "      \\  \\                   '     `'.             \n" +
                        "   .--'\\  |  '         '       .      `-._         \n" +
                        "  /`;--' /_.'          .                  `-.      \n" +
                        "  |  `--`        /               \\           \\     \n" +
                        "   \\       .'   '                 '-.        |     \n" +
                        "    \\   '               '          __\\       |     \n" +
                        "     '.      .                 _.-'  `)     /      \n" +
                        "       '-._                _.-' `| .-`   _.'       \n" +
                        "           `'--....____.--'|     (`  _.-'          \n" +
                        "                    /  | |  \\     `\"`              \n" +
                        "                    \\__/ \\__/                      ";

        artArea.setText(asciiArt);

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