package org.signalml.plugin.bookreporter.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.signalml.app.document.TagDocument;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.SignalMLException;

/**
 * @author piotr@develancer.pl
 */
public class BookReporterResultFrame extends javax.swing.JFrame {

	private static final Color[] COLORS = {
		Color.getHSBColor(0.00f, 0.8f, 0.8f),
		Color.getHSBColor(0.25f, 0.8f, 0.8f),
		Color.getHSBColor(0.50f, 0.8f, 0.8f),
		Color.getHSBColor(0.70f, 0.8f, 0.8f),
		Color.getHSBColor(0.90f, 0.8f, 0.8f)
	};

	private final BookReporterChartExportDialog exportDialog;

	private final CombinedDomainXYPlot plot;

	private final JFreeChart chart;

	private final HashMap<String, Paint> colors;

	private StyledTagSet tags;
	
	private int nextColor = 0;
	
	/**
	 * Creates new form BookReporterResultFrame
	 */
	public BookReporterResultFrame() {
		initComponents();
		exportDialog = new BookReporterChartExportDialog(this, true);
		plot = new CombinedDomainXYPlot();
		chart = new JFreeChart(plot);
		colors = new HashMap<String, Paint>();
		tags = null;

		chart.setBackgroundPaint(Color.WHITE);

		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanelContainer.add(chartPanel);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        pngSaveButton = new javax.swing.JButton();
        tagsSaveButton = new javax.swing.JButton();
        chartPanelContainer = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Book reporting result");
        setMinimumSize(new java.awt.Dimension(300, 300));

        pngSaveButton.setText("Export diagram as PNG");
        pngSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pngSaveButtonActionPerformed(evt);
            }
        });

        tagsSaveButton.setText("Export tags");
        tagsSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tagsSaveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(pngSaveButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tagsSaveButton))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(pngSaveButton)
                .addComponent(tagsSaveButton))
        );

        chartPanelContainer.setBackground(java.awt.Color.white);
        chartPanelContainer.setLayout(new java.awt.GridLayout(0, 1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chartPanelContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chartPanelContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pngSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pngSaveButtonActionPerformed
		exportDialog.setVisible(true);
		{
			File outputFile = exportDialog.getSelectedFile();
			if (outputFile != null) {
				BufferedImage image = this.generateBufferedImage(exportDialog.getPixelWidth(), exportDialog.getPixelHeight(), exportDialog.getScale());
				try {
					ImageIO.write(image, "png", outputFile);
					JOptionPane.showMessageDialog(this, "Image was saved successfully!", "Image saved to file", JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(this, ex.getMessage(), "Error saving image", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
    }//GEN-LAST:event_pngSaveButtonActionPerformed

    private void tagsSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tagsSaveButtonActionPerformed
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("tag files", "tag"));
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File outputFile = fileChooser.getSelectedFile();
			if (outputFile != null) {
				try {
					TagDocument document = new TagDocument(tags);
					try {
						document.setBackingFile(outputFile);
						document.saveDocument();
						JOptionPane.showMessageDialog(this, "Tags were saved successfully!", "Tags saved to file", JOptionPane.INFORMATION_MESSAGE);
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(this, ex.getMessage(), "Error saving tags", JOptionPane.ERROR_MESSAGE);
					}
				} catch (SignalMLException ex) {
					JOptionPane.showMessageDialog(this, ex.getMessage(), "Error processing tags", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
    }//GEN-LAST:event_tagsSaveButtonActionPerformed

	public void addPlotToPanel(XYPlot plot) {
		this.plot.add(plot);
		String key = (String) plot.getDataset().getSeriesKey(0);
		Paint existingColor = colors.get(key);
		XYItemRenderer renderer = plot.getRenderer();
		if (existingColor == null) {
			existingColor = COLORS[nextColor];
			nextColor = (nextColor + 1) % COLORS.length;
			colors.put(key, existingColor);
		} else {
			renderer.setSeriesVisibleInLegend(0, false);
		}
		renderer.setSeriesPaint(0, existingColor);
	}

	public void setTags(StyledTagSet tags) {
		this.tags = tags;
		tagsSaveButton.setEnabled(this.tags != null);
	}

	public void setTimeAxis(ValueAxis timeAxis) {
		if (timeAxis != null) {
			this.plot.setDomainAxis(timeAxis);
		}
	}

	private BufferedImage generateBufferedImage(int width, int height, float scale) {
		double widthForDraw = width / scale;
		double heightForDraw = height / scale;

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		AffineTransform saved = graphics.getTransform();
		graphics.transform(AffineTransform.getScaleInstance(scale, scale));
		chart.draw(graphics, new Rectangle2D.Double(0, 0, widthForDraw, heightForDraw));
		graphics.setTransform(saved);
		graphics.dispose();
		return image;
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel chartPanelContainer;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton pngSaveButton;
    private javax.swing.JButton tagsSaveButton;
    // End of variables declaration//GEN-END:variables
}
