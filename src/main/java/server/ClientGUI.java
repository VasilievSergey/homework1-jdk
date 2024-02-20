package server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ClientGUI extends JFrame {
    public static final int WIDTH = 400;
    public static final int HEIGHT = 300;

    private ServerWindow server;
    private boolean isConnected;

    JTextArea log;
    JTextField tfIPAddress;
    JTextField tfPort;
    JTextField tfLogin;
    JTextField tfMessage;
    JPasswordField password;
    JButton btnLogin;
    JButton btnSend;
    JPanel topPanel;


    private String name;


    public ClientGUI(ServerWindow server) {
        this.server = server;
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setTitle("Chat client");
        setLocation(server.getX() - 500, server.getY());

        createPanel();

        setVisible(true);
    }

    private void createPanel() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createLog());
        add(createFooter(), BorderLayout.SOUTH);
    }

    private Component createHeaderPanel() {
        topPanel = new JPanel(new GridLayout(2, 3));
        tfIPAddress = new JTextField("127.0.0.1");
        tfPort = new JTextField("9000");
        tfLogin = new JTextField("Sergey Vasiliev");
        password = new JPasswordField("password");
        btnLogin = new JButton("login");
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer();
            }
        });

        topPanel.add(tfIPAddress);
        topPanel.add(tfPort);
        topPanel.add(new JPanel(), BorderLayout.EAST);
        topPanel.add(tfLogin);
        topPanel.add(password);
        topPanel.add(btnLogin);

        return topPanel;
    }

    private Component createLog() {
        log = new JTextArea();
        log.setEditable(false);
        return new JScrollPane(log);
    }

    private Component createFooter() {
        JPanel panel = new JPanel(new BorderLayout());
        tfMessage = new JTextField();
        tfMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n' && isConnected) {
                    pushMessage();
                }
            }
        });
        btnSend = new JButton("send");
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                pushMessage();
            }
        });
        panel.add(tfMessage);
        panel.add(btnSend, BorderLayout.EAST);
        return panel;
    }

    public void pushMessage() {
        if (isConnected) {
            String text = tfMessage.getText();
            if (!text.equals("")) {
                server.pushMessage(name + ": " + text);
                tfMessage.setText("");
            }
        } else {
            addLog("Нет подключения к серверу");
        }

    }

    public void pullAnswer(String text) {
        addLog(text);
    }

    private void addLog(String text) {
        log.append(text + "\n");
    }

    private void connectToServer() {
        if (server.connectUser(this)) {
            addLog("Вы подключились\n");
            topPanel.setVisible(false);
            isConnected = true;
            name = tfLogin.getText();
            String log = server.readLog();
            if (log != null) {
                addLog(log);
            }
        } else {
            addLog("Подключение не успешно");
        }
    }

    public void disconnectFromServer() {
        if (isConnected) {
            topPanel.setVisible(true);
            isConnected = false;
            server.disconnectUser(this);
            addLog("Вы отключены от сервера");
        }
    }
}