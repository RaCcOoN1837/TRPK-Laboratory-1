package sample;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.transform.Affine;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    public AnchorPane mainPane;
    public Canvas mainCanvas;
    public Slider sliderScale;
    public TextField textFieldRadius;
    public Label labelResult;

    public static double radius;
    public static double polygonSide;

    public double pressedX;
    public double pressedY;
    public double offsetX;
    public double offsetY;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        sliderScale.valueProperty().addListener((observable, oldValue, newValue) -> {
            draw();
        });

        // Делаем Canvas резиновым.
        mainCanvas.widthProperty().bind(mainPane.widthProperty().subtract(320));
        mainCanvas.widthProperty().addListener((observable, oldValue, newValue) -> {
            double number = (Double) newValue / (Double) oldValue;
            double difference = number - 1;
            radius = radius * (number - (difference / 2));
            draw();
        });
        mainCanvas.heightProperty().bind(mainPane.heightProperty().subtract(40));
        mainCanvas.heightProperty().addListener((observable, oldValue, newValue) -> {
            double number = (Double) newValue / (Double) oldValue;
            double difference = number - 1;
            radius = radius * (number - (difference / 2));
            draw();
        });
    }

    void draw() {

        double dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        double zoom = dpi / 2.54 * sliderScale.getValue() / 100;
        GraphicsContext context = mainCanvas.getGraphicsContext2D();

        context.setFill(Color.web("#FFFFFF"));
        context.fillRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());

        context.save(); // Сохранение состояния матрицы.
        Affine transform = context.getTransform(); // Запрос матрицы преобразования.
        transform.appendTranslation(mainCanvas.getWidth() / 2 + this.offsetX, mainCanvas.getHeight() / 2 + this.offsetY);
        transform.appendScale(zoom, - zoom); // Увеличиваем масштаб.
        context.setTransform(transform); // Применяем изменения.

        // Сторона 5-угольника.
        polygonSide = 2 * radius * Math.tan(Math.toRadians(36));

        // Расстояние от центра до вершины звезды.
        double longLine = radius + Math.sqrt(
                Math.pow(((polygonSide * Math.sin(Math.toRadians(72))) / Math.sin(Math.toRadians(36))), 2) -
                        Math.pow((radius * Math.tan(Math.toRadians(36))), 2));

        // Расстояние от центра до вершины 5-угольника.
        double shortLine = polygonSide / (2 * Math.sin(Math.toRadians(36)));

        // Устанавливаем толщину и цвет линии.
        context.setLineWidth(2. / zoom);
        context.setStroke(Color.web("#000000"));

        // Закрашиваем 5-угольник.
        context.setFill(Color.web("#F11A38"));
        context.fillPolygon(
                new double[]{
                        shortLine * Math.cos(Math.toRadians(54)),
                        shortLine * Math.cos(Math.toRadians(126)),
                        shortLine * Math.cos(Math.toRadians(198)),
                        shortLine * Math.cos(Math.toRadians(270)),
                        shortLine * Math.cos(Math.toRadians(342))
                },
                new double[]{
                        shortLine * Math.sin(Math.toRadians(54)),
                        shortLine * Math.sin(Math.toRadians(126)),
                        shortLine * Math.sin(Math.toRadians(198)),
                        shortLine * Math.sin(Math.toRadians(270)),
                        shortLine * Math.sin(Math.toRadians(342))
                },
                5
        );

        // Закрашиваем окружность.
        context.setFill(Color.web("#FFFFFF"));
        context.fillArc(- radius, - radius, radius * 2, radius * 2, 0, 360, ArcType.OPEN);

        // Рисуем окружность.
        context.strokeArc(- radius, - radius, radius * 2, radius * 2, 0, 360, ArcType.OPEN);

        // Рисуем звезду.
        context.strokePolygon(
                new double[]{
                        longLine * Math.cos(Math.toRadians(18)),
                        longLine * Math.cos(Math.toRadians(162)),
                        longLine * Math.cos(Math.toRadians(306)),
                        longLine * Math.cos(Math.toRadians(90)),
                        longLine * Math.cos(Math.toRadians(234))
                },
                new double[]{
                        longLine * Math.sin(Math.toRadians(18)),
                        longLine * Math.sin(Math.toRadians(162)),
                        longLine * Math.sin(Math.toRadians(306)),
                        longLine * Math.sin(Math.toRadians(90)),
                        longLine * Math.sin(Math.toRadians(234))
                },
                5
        );

        context.restore(); // Восстановление состояния матрицы.
    }

    private String checkInput(){

        String errorMessage ="";
        String inputText = textFieldRadius.getText();

        if (isEmpty(inputText)) {
            errorMessage = "Введите радиус окружности!";
        }
        else if (!isInteger(inputText)) {
            errorMessage = "Введите целое число!";
        }
        else if (Integer.parseInt(inputText) <= 0 || Integer.parseInt(inputText) >= 6) {
            errorMessage = "Введите число в диапазоне [1, 5] !";
        }
        return errorMessage;
    }

    private boolean isEmpty(String string){
        if (string.equals(""))
            return true;
        return false;
    }

    private static boolean isInteger(String string)
    {
        try
        {
            int number = Integer.parseInt(string);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public static double calculateSquare(double radius) {
        double polygonSide = 2 * radius * Math.tan(Math.toRadians(36));;
        return (radius * (polygonSide * 5 / 2) - (Math.PI * radius * radius));
    }

    public void onButtonPressed(ActionEvent actionEvent) {
        if (!checkInput().equals("")){

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ошибка.");
            alert.setHeaderText("Некорректный ввод.");
            alert.setContentText(checkInput());

            alert.showAndWait();
            return;
        } else {
            radius = Integer.parseInt(textFieldRadius.getText());
            this.sliderScale.setValue(100.0);
            draw();
            this.labelResult.setText(String.format("%.3f", calculateSquare(radius)) + " см²");
        }
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        this.offsetX += mouseEvent.getX() - this.pressedX;
        this.offsetY += mouseEvent.getY() - this.pressedY;
        this.pressedX = mouseEvent.getX();
        this.pressedY = mouseEvent.getY();
        draw();
    }

    public void onMousePressed(MouseEvent mouseEvent) {
        this.pressedX = mouseEvent.getX();
        this.pressedY = mouseEvent.getY();
    }
}
