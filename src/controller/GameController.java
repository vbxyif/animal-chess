package controller;


import listener.GameListener;
import model.Chessboard;
import model.ChessboardPoint;
import model.PlayerColor;
import view.CellComponent;
import view.ChessComponent;
import view.ChessboardComponent;
import view.MessageText;

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


    public GameController(ChessboardComponent view, Chessboard model, MessageText roundText) throws IOException {
        this.view = view;
        this.model = model;
        this.roundText = roundText;
        this.currentPlayer = PlayerColor.BLUE;

        view.registerController(this);
        view.initiateChessComponent(model);
        view.repaint();
        round = 1;
    }

    public boolean canUndo() {
        return stringWriter.length() > 0;
    }

    public void undo() {
        String[] lines = stringWriter.toString().split("\n");
        for (String line : lines) {
            if (line.equals(lines[lines.length - 1])) {
                break;
            }
            if (line.matches("\\d+.\\d")) {
                round = Double.parseDouble(line);
                line = " ";
            } else if(line.matches("\\D")){
                currentPlayer = line.equals("BLUE") ? PlayerColor.BLUE : PlayerColor.RED;
                line = " ";
            }else if (match(line)) {
                matchCapture(line);
            } else {
                matchMove(line);
            }
            if (!line.equals(" ")) {
                stringWriter.append(line).append("\n");
            }
        }
        stringWriter = new StringBuilder();
        for (String line : lines) {
            if (line.equals(lines[lines.length - 1])) {
                break;
            }
            stringWriter.append(line).append("\n");
        }
        roundText.setText(String.valueOf((int) round));
        roundText.setForeground(currentPlayer.getColor());
        view.repaint();
    }

    public void save(String str) throws IOException {
        File newGameFile = new File(String.format("src/saves/%s.txt", str));
        savesFileWriter = new SavesFileWriter(newGameFile);
        savesFileWriter.write(stringWriter.toString());
        savesFileWriter.write(round + "\n");
        savesFileWriter.write(currentPlayer.toString());
        savesFileWriter.close();
    }

    public void load(String str) throws IOException {
        stringWriter = new StringBuilder();
        File newGameFile = new File(str);
        savesFileReader = new FileReader(newGameFile);
        BufferedReader bufferedReader = new BufferedReader(savesFileReader);
        for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
            if (line.matches("\\d+.\\d")) {
                round = Double.parseDouble(line);
                line = " ";
            } else if(line.matches("\\D")){
                currentPlayer = line.equals("B") ? PlayerColor.BLUE : PlayerColor.RED;
                System.out.println(currentPlayer);
                break;
            }else if (match(line)) {
                matchCapture(line);
            } else {
                matchMove(line);
            }
            if (!line.equals(" ")) {
                stringWriter.append(line).append("\n");
            }
        }
        bufferedReader.close();
        savesFileReader.close();
        roundText.setText(String.valueOf((int) round));
        roundText.setForeground(currentPlayer.getColor());
        view.repaint();
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
        String pattern = "(\\(\\D,\\D\\)) move from (\\(\\d,\\d\\)) to (\\(\\d,\\d\\))";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        return !m.find();
    }
    private void matchMove(String str) {

        String pattern = "(\\(\\D,\\D\\)) move from (\\(\\d,\\d\\)) to (\\(\\d,\\d\\))";
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
    }

    private void matchCapture(String str) {

        String pattern = "(\\(\\D,\\D\\)) capture (\\(\\D,\\D\\)) from (\\(\\d,\\d\\)) to (\\(\\d,\\d\\))";
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
    }

    // after a valid move swap the player
    private void swapColor() {
        currentPlayer = currentPlayer == PlayerColor.BLUE ? PlayerColor.RED : PlayerColor.BLUE;
        round += 0.5;
        roundText.setText(String.valueOf((int) round));
        roundText.setForeground(currentPlayer.getColor());
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

    private void writeOperate(ChessboardPoint src, ChessboardPoint dest) throws IOException {
        ChessComponent chess = view.removeChessComponentAtGrid(src);
        if (model.hasChessPiece(dest)) {
            ChessComponent target = view.removeChessComponentAtGrid(dest);
            model.captureChessPiece(src, dest);
            view.setChessComponentAtGrid(dest, chess);
            stringWriter.append(String.format("(%s,%s) capture (%s,%s) from %s to %s\n", chess.getOwner(), chess.getName(), target.getOwner(), target.getName(), src, dest));
        } else {
            model.moveChessPiece(src, dest);
            view.setChessComponentAtGrid(dest, chess);
            stringWriter.append(String.format("(%s,%s) move from %s to %s\n", chess.getOwner(), chess.getName(), src, dest));
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
            for (CellComponent[] cells : view.getGridComponents()) {
                for (CellComponent cell : cells) {
                    cell.setValidMove(false);
                }
            }
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
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 7; j++) {
                        ChessboardPoint point1 = new ChessboardPoint(i, j);
                        if (model.isValidMove(point, point1) && model.isValidCapture(point, point1)) {
                            view.getGridComponentAt(point1).setValidMove(true);
                        }
                    }
                }
                component.setSelected(true);
                view.repaint();
            }
        } else if (selectedPoint.equals(point)) {
            selectedPoint = null;
            for (CellComponent[] cells : view.getGridComponents()) {
                for (CellComponent cell : cells) {
                    cell.setValidMove(false);
                }
            }
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
                for (CellComponent[] cells : view.getGridComponents()) {
                    for (CellComponent cell : cells) {
                        cell.setValidMove(false);
                    }
                }

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
