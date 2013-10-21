package player;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

import character.GameAvatar;
import character.GameHouse;
import character.GamePlayer;
import common.CDirection;
import common.FedConfig;

public class PlayerView extends JPanel implements ActionListener {
    private Timer      timer;
    private PlayerMain playerMain;
    private JFrame     frame;

    private JPanel     statusPanel;
    private JLabel     statusLabel;

    public PlayerView(PlayerMain playerMain, JFrame frame) {
        this.timer = new Timer(30, this);
        this.playerMain = playerMain;
        this.frame = frame;

        this.statusPanel = new JPanel();
        this.statusLabel = new JLabel();
        this.initialize();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawObjects(g);
    }

    public void drawObjects(Graphics g) {

        /** draw houses */
        for (Iterator<GameHouse> iterator = this.playerMain.getRtiFedAmbHelper().getHouseList().values().iterator(); iterator.hasNext();) {
            GameHouse house = iterator.next();
            if (this.playerMain.getGamePlayer().isVisible(house.getCoordinate(), FedConfig.getPlayerDimension(), FedConfig.getHouseDimension()) == true) {
                house.drawItself(g);
            }
        }

        /** draw avatars */
        for (Iterator<GameAvatar> iterator = this.playerMain.getRtiFedAmbHelper().getAvatarList().values().iterator(); iterator.hasNext();) {
            GameAvatar avatar = iterator.next();
            if (this.playerMain.getGamePlayer().isVisible(avatar.getCoordinate(), FedConfig.getPlayerDimension(), FedConfig.getAvatarDimension()) == true) {
                avatar.drawItself(g);
            }
        }

        /** draw players */
        for (Iterator<GamePlayer> iterator = this.playerMain.getRtiFedAmbHelper().getPlayerList().values().iterator(); iterator.hasNext();) {
            GamePlayer player2 = iterator.next();
            // draw if visible
            if (this.playerMain.getGamePlayer().isVisible(player2.getCoordinate(), FedConfig.getPlayerDimension(), FedConfig.getPlayerDimension()) == true) {
                player2.drawItself(g);
            }
        }

        this.playerMain.getGamePlayer().drawItself(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
        updateGameInformation();
    }

    private void initialize() {
        this.timer.start();
        this.statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.frame.add(statusPanel, BorderLayout.SOUTH);
        this.statusPanel.setPreferredSize(new Dimension(frame.getWidth(), 20));
        this.statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        this.statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        this.statusPanel.add(statusLabel);
        this.addKeyListener(new TAdapter());
        this.setFocusable(true);
    }

    private void updateGameInformation() {
        int houseCount = this.playerMain.getRtiFedAmbHelper().getHouseList().size();
        int avatarCount = this.playerMain.getRtiFedAmbHelper().getAvatarList().size();
        int playerCount = this.playerMain.getRtiFedAmbHelper().getPlayerList().size() + 1;

        this.statusLabel.setText("Count [  House: " + houseCount + "   |   " + "PlayerMain: " + playerCount + "   |   " + "Avatar: " + avatarCount
                + "  ]");
    }

    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int keycode = e.getKeyCode();

            switch (keycode) {

            case KeyEvent.VK_LEFT:
                PlayerView.this.playerMain.getGamePlayer().move(CDirection.LEFT);
                break;

            case KeyEvent.VK_RIGHT:
                PlayerView.this.playerMain.getGamePlayer().move(CDirection.RIGHT);
                break;

            case KeyEvent.VK_DOWN:
                PlayerView.this.playerMain.getGamePlayer().move(CDirection.DOWN);

                break;

            case KeyEvent.VK_UP:
                PlayerView.this.playerMain.getGamePlayer().move(CDirection.UP);

                break;

            case KeyEvent.VK_SPACE:
                PlayerView.this.playerMain.getGamePlayer().modeSwitch();
                break;

            }
        }
    }

}
