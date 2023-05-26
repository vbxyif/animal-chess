package controller;


import listener.GameListener;
import model.Chessboard;
import model.ChessboardPoint;
import model.PlayerColor;
import view.CellComponent;
import view.ChessComponent;
import view.ChessboardComponent;
import view.MessageText;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Controller is the connection between model and view,
 * when a Controller receive a request from a view, the Controller
 * analyzes and then hands over to the model for processing
 * [in this demo the request methods are onPlayerClickCell() and onPlayerClickChessPiece()]
 */
public class GameController implements GameListener {


    private final Chessboard model;
    private final ChessboardComponent view;
    private final MessageText roundText;
    private PlayerColor currentPlayer;

    // Record whether there is a selected piece before
    private ChessboardPoint selectedPoint;
    private double round;
    public static SavesFileWriter savesFileWriter;
    public static FileReader savesFileReader;
    private static StringBuilder stringWriter = new StringBuilder();
    private Clip chessClip;
    private Clip backgroundClip;


    public GameController(ChessboardComponent view, Chessboard model) throws IOException {
        this.view = view;
        this.model = model;
        this.currentPlayer = PlayerColor.BLUE;
        round = stringWriter.toString().split("\n").length * 0.5 + 1;
        this.roundText = new MessageText(String.valueOf((int) round), currentPlayer.getColor());
        setBackgroundClip("background");
        backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
        view.registerController(this);
        try {
            view.initiateChessComponent(model);
        } catch (UnsupportedAudioFileException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        view.repaint();
    }

    private void setBackgroundClip(String str) {
        try {
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(AudioSystem.getAudioInputStream(new File("src/wav/" + str + ".wav")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Clip getBackgroundClip() {
        return backgroundClip;
    }

    public void restart() {
        for (CellComponent[] cells : view.getGridComponents()) {
            for (CellComponent cell : cells) {
                cell.setValidMove(false);
            }
        }
        stringWriter = new StringBuilder();
        changeText();
    }

    public boolean canUndo() {
        return stringWriter.length() > 0;
    }

    public void undo() {
        String[] lines = stringWriter.toString().split("\n");
        stringWriter = new StringBuilder();
        for (String line : lines) {
            System.out.println(line);
            if (line.equals(lines[lines.length - 1])) {
                break;
            } else if (line.matches("\\d+.\\d")) {
                continue;
            } else if (match(line)) {
                matchCapture(line);
            } else {
                matchMove(line);
            }
            stringWriter.append(line).append("\n");
        }

        System.out.println(round + " " + lines.length);
        System.out.println(stringWriter.toString());
        changeText();
        view.repaint();
    }

    public boolean save(String str) throws IOException {
        boolean result;
        File newGameFile = new File(String.format("src/saves/%s.txt", str));
        if (newGameFile.createNewFile()) {
            System.out.println("文件创建成功");
            result = true;
        } else {
            System.out.println("文件创建失败");
            result = false;
        }
        savesFileWriter = new SavesFileWriter(newGameFile);
        savesFileWriter.write(stringWriter.toString());
        savesFileWriter.write(round + "\n");
        savesFileWriter.write(currentPlayer.toString());
        savesFileWriter.close();
        return result;
    }

    public void load(String str) throws IOException {
        backgroundClip.close();
        stringWriter = new StringBuilder();
        File newGameFile = new File(str);
        savesFileReader = new FileReader(newGameFile);
        BufferedReader bufferedReader = new BufferedReader(savesFileReader);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                        changeText();
                        if (line.matches("\\d+.\\d")) {
                            round = Double.parseDouble(line);
                            continue;
                        } else if(line.matches("\\D")){
                            currentPlayer = line.equals("B") ? PlayerColor.BLUE : PlayerColor.RED;
                            break;
                        }else if (match(line)) {
                            matchCapture(line);
                        } else {
                            matchMove(line);
                        }
                        stringWriter.append(line).append("\n");
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    bufferedReader.close();
                    savesFileReader.close();
                    JOptionPane.showMessageDialog(null, "加载成功");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } ).start();
        /*roundText.setText(String.valueOf((int) round));
        roundText.setForeground(currentPlayer.getColor());*/
        view.repaint();
    }

    public boolean delete(String str) throws IOException {
        File newGameFile = new File(str);
        if (newGameFile.delete()) {
            System.out.println("删除成功");
            return true;
        } else {
            System.out.println("删除失败");
            return false;
        }
    }

    private ChessboardPoint text2point(String text) {
        Pattern r = Pattern.compile("\\d");
        Matcher matcher = r.matcher(text);
        int[] coordinate = new int[2];
        int i = 0;
        while (matcher.find()) {
            coordinate[i] = Integer.parseInt(matcher.group());
            i++;
        }
        return new ChessboardPoint(coordinate[0], coordinate[1]);
    }

    //regex to match the stringWriter
    private boolean match(String str) {
        String pattern = "(\\(\\D,\\D+\\)) move from (\\(\\d,\\d\\)) to (\\(\\d,\\d\\))";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        return !m.find();
    }
    private void matchMove(String str) {

        String pattern = "(\\(\\D,\\D+\\)) move from (\\(\\d,\\d\\)) to (\\(\\d,\\d\\))";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        String src = "";
        String dest = "";
        if (m.find()) {
            src = m.group(2);
            dest = m.group(3);
        }

        ChessboardPoint srcPoint = text2point(src);
        ChessboardPoint destPoint = text2point(dest);

        ChessComponent chess = view.removeChessComponentAtGrid(srcPoint);
        model.moveChessPiece(srcPoint, destPoint);
        view.setChessComponentAtGrid(destPoint, chess);
        swapColor();
    }

    private void matchCapture(String str) {

        String pattern = "(\\(\\D,\\D+\\)) capture (\\(\\D,\\D\\)) from (\\(\\d,\\d\\)) to (\\(\\d,\\d\\))";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        String src = "";
        String dest = "";
        if (m.find()) {
            src = m.group(3);
            dest = m.group(4);
        }

        ChessboardPoint srcPoint = text2point(src);
        ChessboardPoint destPoint = text2point(dest);

        ChessComponent chess = view.removeChessComponentAtGrid(srcPoint);
        view.removeChessComponentAtGrid(destPoint);
        model.captureChessPiece(srcPoint, destPoint);
        view.setChessComponentAtGrid(destPoint, chess);
        swapColor();
    }

    // after a valid move swap the player
    private void swapColor() {
        currentPlayer = currentPlayer == PlayerColor.BLUE ? PlayerColor.RED : PlayerColor.BLUE;
        changeText();
    }

    private void changeText() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                view.repaint();
                round = stringWriter.toString().split("\n").length * 0.5 + 1;
                roundText.setText(String.valueOf((int) round));
                roundText.setForeground(currentPlayer.getColor());
            }
        });
    }

    public MessageText getRoundText() {
        return roundText;
    }

    private boolean win(ChessboardPoint point) {
        // TODO: Check the board if there is a winner--Done
        return point.equals(ChessboardComponent.getDenRed()) || point.equals(ChessboardComponent.getDenBlue())
                || model.getBlue().isEmpty() || model.getRed().isEmpty();
    }

    private void afterWin() {
        System.out.println("游戏结束！");
        //JOptionPane.showMessageDialog(null,"游戏结束！");
        roundText.setFont(new Font("Black", Font.BOLD, 100));
        roundText.setText("Win!");
        view.disableEvents();
    }

    public Clip getChessClip() {
        return chessClip;
    }

    private void startClip(String str) throws LineUnavailableException {
        chessClip = AudioSystem.getClip();
        try {
            chessClip.open(AudioSystem.getAudioInputStream(new File("src/wav/" + str +".wav")));
        } catch (IOException | UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }
        new Thread(chessClip::start).start();
    }

    private void writeOperate(ChessboardPoint src, ChessboardPoint dest) throws IOException {
        ChessComponent chess = view.removeChessComponentAtGrid(src);
        if (model.hasChessPiece(dest)) {
            ChessComponent target = view.removeChessComponentAtGrid(dest);
            model.captureChessPiece(src, dest);
            view.setChessComponentAtGrid(dest, chess);
            try {
                startClip("capture");
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
            stringWriter.append(String.format("(%s,%s) capture (%s,%s) from %s to %s\n", chess.getOwner(), chess.getName(), target.getOwner(), target.getName(), src, dest));
        } else {
            model.moveChessPiece(src, dest);
            view.setChessComponentAtGrid(dest, chess);
            try {
                startClip("move");
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
            stringWriter.append(String.format("(%s,%s) move from %s to %s\n", chess.getOwner(), chess.getName(), src, dest));
        }
    }

    private void setInvalidMove() {
        for (CellComponent[] cells : view.getGridComponents()) {
            for (CellComponent cell : cells) {
                cell.setValidMove(false);
            }
        }
    }

    private void setValidMove(ChessboardPoint point) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                ChessboardPoint point1 = new ChessboardPoint(i, j);
                if (model.isValidMove(point, point1) && model.isValidCapture(point, point1)) {
                    view.getGridComponentAt(point1).setValidMove(true);
                }
            }
        }
    }
    // click an empty cell
    @Override
    public void onPlayerClickCell(ChessboardPoint point, CellComponent component) {
        if (selectedPoint != null && model.isValidMove(selectedPoint, point)) {
            try {
                writeOperate(selectedPoint, point);
            } catch (IOException e) {
                System.out.println("bugs");
                throw new RuntimeException(e);
            }
            selectedPoint = null;
            setInvalidMove();
            view.repaint();
            if (win(point)) {
                afterWin();
            } else {
                swapColor();
            }
            // TODO: if the chess enter Dens or Traps and so on
        }
    }



    // click a cell with a chess
    @Override
    public void onPlayerClickChessPiece(ChessboardPoint point, ChessComponent component) {
        if (selectedPoint == null) {
            if (model.getChessPieceOwner(point).equals(currentPlayer)) {
                selectedPoint = point;
                setValidMove(point);
                component.setSelected(true);
                view.repaint();
            }
        } else if (selectedPoint.equals(point)) {
            selectedPoint = null;
            setInvalidMove();
            component.setSelected(false);
            view.repaint();
        } else if (!model.getChessPieceOwner(point).equals(currentPlayer)) {
            if (model.isValidCapture(selectedPoint, point)) {

                try {
                    writeOperate(selectedPoint, point);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                selectedPoint = null;
                setInvalidMove();
                view.repaint();
                if (win(point)) {
                    afterWin();
                } else {
                    swapColor();
                }
            }
        }
        // TODO: Implement capture function--Done
    }
}
