import java.awt.Dimension;
import java.util.Optional;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

class UserView extends JFrame {

    private static final int MIN_SELECTABLE_VALUE = 15;
    private static final int MAX_SELECTABLE_VALUE = 45;
    private static final int DEFAULT_VALUE = (MAX_SELECTABLE_VALUE + MIN_SELECTABLE_VALUE) / 2;

    private JTextField tempValue;
    private JSlider temp;
    private int currentUserPref = DEFAULT_VALUE;
    private boolean hasUserChangedValue = false;

    public UserView(){
        setTitle("User Thermostat");
        setSize(400,160);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setContentPane(mainPanel);

        JPanel temperature = new JPanel();
        temperature.setLayout(new BoxLayout(temperature, BoxLayout.Y_AXIS));

        JPanel temperature1 = new JPanel();
        temperature1.setLayout(new BoxLayout(temperature1, BoxLayout.X_AXIS));

        tempValue = new JTextField(5);
        tempValue.setText("");
        tempValue.setSize(100, 30);
        tempValue.setMinimumSize(tempValue.getSize());
        tempValue.setMaximumSize(tempValue.getSize());
        tempValue.setEditable(false);

        temperature1.add(new JLabel("User pref temperature:"));
        temperature1.add(Box.createRigidArea(new Dimension(0,5)));
        temperature1.add(tempValue);

        temp = new JSlider(
                JSlider.HORIZONTAL,
                MIN_SELECTABLE_VALUE,
                MAX_SELECTABLE_VALUE,
                currentUserPref
        );
        temp.setSize(300, 60);
        temp.setMinimumSize(temp.getSize());
        temp.setMaximumSize(temp.getSize());
        temp.setMajorTickSpacing(10);
        temp.setMinorTickSpacing(1);
        temp.setPaintTicks(true);
        temp.setPaintLabels(true);

        temp.addChangeListener((ChangeEvent ev) -> {
            this.hasUserChangedValue = true;
            final int value = temp.getValue();
            this.updateUserTargetTemperature(value);
            tempValue.setText("" + value);
        });

        temperature.add(temperature1);
        temperature.add(temp);

        mainPanel.add(temperature);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public synchronized Optional<Integer> getCurrentUserTemperature() {
        return this.hasUserChangedValue ? Optional.of(this.currentUserPref) : Optional.empty();
    }

    private synchronized void updateUserTargetTemperature(final int temp) {
        this.currentUserPref = temp;
    }

}
