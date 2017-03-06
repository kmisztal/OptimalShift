package pl.edu.misztal.OptimalShift;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;
import de.erichseifert.gral.ui.InteractivePanel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.KeyStroke;

/**
 *
 * @author chris_pi
 */
public class LinePlotTest extends JFrame {

    public LinePlotTest(List<Cluster>... dataset) {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // on ESC key close frame
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel"); //$NON-NLS-1$
        getRootPane().getActionMap().put("Cancel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        setSize(600, 400);

        DataTable data = new DataTable(Double.class, Double.class);
        for (List<Cluster> d : dataset) {
            d.stream().forEach((p) -> {
                data.add(p.getMean().get(0, 0), p.getMean().get(1, 0));
            });
        }
        XYPlot plot = new XYPlot(data);
        getContentPane().add(new InteractivePanel(plot));
        plot.addPointRenderer(data, new DefaultPointRenderer2D());
    }
}
