import edu.stanford.math.plex4.api.Plex4;
import edu.stanford.math.plex4.examples.PointCloudExamples;
import edu.stanford.math.plex4.homology.barcodes.BarcodeCollection;
import edu.stanford.math.plex4.homology.barcodes.Interval;
import edu.stanford.math.plex4.homology.chain_basis.Simplex;
import edu.stanford.math.plex4.homology.interfaces.AbstractPersistenceAlgorithm;
import edu.stanford.math.plex4.metric.impl.EuclideanMetricSpace;
import edu.stanford.math.plex4.metric.landmark.LandmarkSelector;
import edu.stanford.math.plex4.metric.landmark.MaxMinLandmarkSelector;
import edu.stanford.math.plex4.streams.impl.LazyWitnessStream;
import edu.stanford.math.plex4.visualization.BarcodeVisualizer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*
Takes WitnessComplexDemo and generates its barcode
*/

public class WitnessComplexVisualization extends JFrame{
    public WitnessComplexVisualization() {
        setLayout( new BoxLayout( getContentPane(), BoxLayout.Y_AXIS ));
        int n = 2000;
        int d = 3;
        int numLandmarkPoints = 80;
        double maxDistance = 0.05;

        double[][] points = PointCloudExamples.getRandomSpherePoints(n, d);
        EuclideanMetricSpace metricSpace = new EuclideanMetricSpace(points);

        LandmarkSelector<double[]> landmarkSelector = new MaxMinLandmarkSelector<double[]>(metricSpace, numLandmarkPoints);

        LazyWitnessStream<double[]> stream = new LazyWitnessStream<double[]>(metricSpace, landmarkSelector, d + 1, maxDistance, 20);
        stream.finalizeStream();


        AbstractPersistenceAlgorithm<Simplex> algorithm = Plex4.getDefaultSimplicialAlgorithm(d + 1);

        BarcodeCollection<Double> intervals = algorithm.computeIntervals(stream);

        setSize(800,600);

        setTitle("Barcode for LazyWitness");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        System.out.println("Barcodes for " + d + "-sphere:");
        System.out.println(intervals);
        Set<Integer> setOfDimensions = intervals.getDimensions();
        for (Integer s : setOfDimensions) {
            List intervalsOfDimension = intervals.getIntervalsAtDimension(s);
            System.out.println("dimension :"+ s);
            System.out.println(intervalsOfDimension);
            BufferedImage plot = null;
            try {
                plot = BarcodeVisualizer.drawBarcode(intervalsOfDimension, "barcode for dimension "+s, 2);
                File outputfile = new File("barcodes/barcode_dimension_"+s+".jpg");
                ImageIO.write(plot, "jpg", outputfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JLabel picLabel = new JLabel(new ImageIcon(plot));
            add(picLabel);
        }
        setVisible(true);

    }
    public static void main(String[] args) {
        new WitnessComplexVisualization();
    }
}
