/*
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2015 University of Dundee. All rights reserved.
 *
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.agents.fsimporter.util;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import ome.formats.importer.ImportContainer;
import ome.formats.importer.ImportEvent;
import ome.formats.importer.ImportEvent.FILE_UPLOAD_BYTES;

import org.apache.commons.io.FileUtils;
import org.openmicroscopy.shoola.env.data.util.StatusLabel;
import org.openmicroscopy.shoola.util.CommonsLangUtils;
import org.openmicroscopy.shoola.util.ui.UIUtilities;

/**
 * Component displaying the status of a specific import.
 *
 * @author Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:donald@lifesci.dundee.ac.uk"
 *         >donald@lifesci.dundee.ac.uk</a>
 * @author Blazej Pindelski, bpindelski at dundee.ac.uk
 * @version 3.0
 * @since 3.0-Beta4
 */
public class StatusLabelView extends JPanel implements PropertyChangeListener {
    /** The text displayed when the file is already selected. */
    public static final String DUPLICATE = "Already processed, skipping";

    /** The text indicating the scanning steps. */
    public static final String SCANNING_TEXT = "Scanning...";

    /** The default text of the component. */
    public static final String DEFAULT_TEXT = "Pending...";

    /** Text to indicate that the import is cancelled. */
    private static final String CANCEL_TEXT = "Cancelled";

    /** Text to indicate that no files to import. */
    private static final String NO_FILES_TEXT = "No Files to Import.";

    /** The width of the upload bar. */
    private static final int WIDTH = 200;

    /** The maximum number of value for upload. */
    private static final int MAX = 100;

    private int step = 0;

    /**
     * The number of processing sets. 1. Importing Metadata 2. Processing Pixels
     * 3. Generating Thumbnails 4. Processing Metadata 5. Generating Objects
     */
    /** Map hosting the description of each step. */
    private static final Map<Integer, String> STEPS;

    /** Map hosting the description of the failure at a each step. */
    private static final Map<Integer, String> STEP_FAILURES;

    static {
        STEPS = new HashMap<Integer, String>();
        STEPS.put(1, "Importing Metadata");
        STEPS.put(2, "Reading Pixels");
        STEPS.put(3, "Generating Thumbnails");
        STEPS.put(4, "Reading Metadata");
        STEPS.put(5, "Generating Objects");
        STEPS.put(6, "Complete");
        STEP_FAILURES = new HashMap<Integer, String>();
        STEP_FAILURES.put(1, "Failed to Import Metadata");
        STEP_FAILURES.put(2, "Failed to Read Pixels");
        STEP_FAILURES.put(3, "Failed to Generate Thumbnails");
        STEP_FAILURES.put(4, "Failed to Read Metadata");
        STEP_FAILURES.put(5, "Failed to Generate Objects");
    }

    /** The label displaying the general import information. */
    private JLabel generalLabel;

    /** Indicate the progress of the upload. */
    private JProgressBar uploadBar;

    /** Indicate the progress of the processing. */
    private JProgressBar processingBar;

    /** The labels displaying information before the progress bars. */
    private List<JLabel> labels;

    /** The file or folder this component is for. */
    private StatusLabel status;

    /**
     * Formats the size of the uploaded data.
     * 
     * @param value
     *            The value to display.
     * @return See above.
     */
    private String formatUpload(long value) {
        StringBuffer buffer = new StringBuffer();
        String v = FileUtils.byteCountToDisplaySize(value);
        String[] values = v.split(" ");
        if (values.length > 1) {
            String u = values[1];
            if (status.getUnits().equals(u))
                buffer.append(values[0]);
            else
                buffer.append(v);
        } else
            buffer.append(v);
        buffer.append("/");
        buffer.append(status.getFileSize());
        return buffer.toString();
    }

    /** Builds and lays out the UI. */
    private void buildUI() {
        labels = new ArrayList<JLabel>();
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(generalLabel);
        JLabel label = new JLabel("Upload");
        label.setVisible(false);
        labels.add(label);
        add(label);
        add(uploadBar);
        add(Box.createHorizontalStrut(5));
        label = new JLabel("Processing");
        label.setVisible(false);
        labels.add(label);
        add(label);
        add(processingBar);
        setOpaque(false);
    }

    /** Initializes the components. */
    private void initialize() {
        generalLabel = new JLabel(DEFAULT_TEXT);
        Font f = generalLabel.getFont();
        Font derived = f.deriveFont(f.getStyle(), f.getSize() - 2);
        uploadBar = new JProgressBar(0, MAX);
        uploadBar.setFont(derived);
        uploadBar.setStringPainted(true);
        Dimension d = uploadBar.getPreferredSize();
        uploadBar.setPreferredSize(new Dimension(WIDTH, d.height));
        processingBar = new JProgressBar(0, STEPS.size());
        processingBar.setStringPainted(true);
        processingBar.setString(DEFAULT_TEXT);
        processingBar.setFont(derived);
        uploadBar.setVisible(false);
        processingBar.setVisible(false);
    }

    /**
     * Creates a new instance.
     * 
     * @param sourceFile
     *            The file associated to that label.
     */
    public StatusLabelView(StatusLabel status) {
        this.status = status;
        this.status.addPropertyChangeListener(this);
        initialize();
        buildUI();
    }

    /**
     * Sets the text of {@link #generalLabel}.
     * 
     * @param text
     *            The value to set.
     */
    public void setText(String text) {
        if (CommonsLangUtils.isEmpty(text)) {
            String value = generalLabel.getText();
            if (DEFAULT_TEXT.equals(value) || SCANNING_TEXT.equals(value))
                generalLabel.setText(text);
        } else
            generalLabel.setText(text);
    }

    /**
     * Displays message when saving rois.
     *
     * @param text
     *            The text displayed
     * @param completed
     *            Update progress bar.
     */
    public void updatePostProcessing(String text, boolean completed) {
        if (!completed) {
            processingBar.setMaximum(processingBar.getMaximum() + 1);
            processingBar.setValue(processingBar.getValue() + 1);
        } else {
            processingBar.setValue(processingBar.getMaximum());
        }
        processingBar.setString(text);
    }

    /** Marks the import has cancelled. */
    public void markedAsCancel() {
        generalLabel.setText(CANCEL_TEXT);
        status.setMarkedAsCancel(true);
    }

    /**
     * Returns <code>true</code> if the import is marked as cancel,
     * <code>false</code> otherwise.
     * 
     * @return See above.
     */
    public boolean isMarkedAsCancel() {
        return status.isMarkedAsCancel();
    }

    /** Marks the import has duplicate. */
    public void markedAsDuplicate() {
        status.setMarkedAsDuplicate(true);
        generalLabel.setText(DUPLICATE);
    }

    /**
     * Returns <code>true</code> if the import is marked as duplicate,
     * <code>false</code> otherwise.
     * 
     * @return See above.
     */
    public boolean isMarkedAsDuplicate() {
        return status.isMarkedAsDuplicate();
    }

    /**
     * Returns the text if an error occurred.
     * 
     * @return See above.
     */
    public String getErrorText() {
        return "";
    }

    /**
     * Returns the number of series.
     * 
     * @return See above.
     */
    public int getSeriesCount() {
        return status.getSeriesCount();
    }

    /**
     * Returns <code>true</code> if the import can be cancelled,
     * <code>false</code> otherwise.
     * 
     * @return See above.
     */
    public boolean isCancellable() {
        return status.isCancellable();
    }

    /**
     * Returns the number of pixels objects created or <code>0</code>.
     * 
     * @return See above.
     */
    public int getNumberOfImportedFiles() {
        return status.getNumberOfImportedFiles();
    }

    /**
     * Returns the size of the upload.
     * 
     * @return See above.
     */
    public long getFileSize() {
        return status.getSizeUpload();
    }

    /**
     * Returns the ID associated to the log file.
     * 
     * @return See above.
     */
    public long getLogFileID() {
        return status.getLogFileID();
    }

    /**
     * Returns <code>true</code> if the upload ever started, <code>false</code>
     * otherwise.
     * 
     * @return See above.
     */
    public boolean didUploadStart() {
        return status.didUploadStart();
    }

    /**
     * Returns the container.
     *
     * @return See above.
     */
    public ImportContainer getImportContainer() {
        return status.getImportContainer();
    }

    /**
     * Sets the import container.
     *
     * @param ic
     *            The value to set.
     */
    public void setImportContainer(ImportContainer ic) {
        status.setImportContainer(ic);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String name = evt.getPropertyName();
        if (StatusLabel.FILES_SET_PROPERTY.equals(name)) {
            Map<File, StatusLabel> files = (Map<File, StatusLabel>) evt
                    .getNewValue();
            StringBuffer buffer = new StringBuffer();
            buffer.append("Importing ");
            buffer.append(files.size());
            buffer.append(" file");
            if (files.size() > 1)
                buffer.append("s");
            generalLabel.setText(buffer.toString());
        } else if (StatusLabel.IMPORT_DONE_PROPERTY.equals(name)) {
            int step = 6;
            processingBar.setValue(step);
            processingBar.setString(STEPS.get(step));
        } else if (StatusLabel.SCANNING_PROPERTY.equals(name)) {
            generalLabel.setText(SCANNING_TEXT);
        } else if (StatusLabel.FILE_UPLOAD_BYTES_PROPERTY.equals(name)) {
            ImportEvent.FILE_UPLOAD_BYTES e = (FILE_UPLOAD_BYTES) evt
                    .getNewValue();
            long v = status.getTotalUploadedSize() + e.uploadedBytes;
            if (status.getSizeUpload() != 0) {
                uploadBar.setValue((int) (v * MAX / status.getSizeUpload()));
            }
            StringBuffer buffer = new StringBuffer();
            if (v != status.getSizeUpload())
                buffer.append(formatUpload(v));
            else
                buffer.append(status.getFileSize());
            buffer.append(" ");
            if (e.timeLeft != 0) {
                String s = UIUtilities.calculateHMSFromMilliseconds(e.timeLeft,
                        true);
                buffer.append(s);
                if (CommonsLangUtils.isNotBlank(s))
                    buffer.append(" Left");
                else
                    buffer.append("complete");
            }
            uploadBar.setString(buffer.toString());
        } else if (StatusLabel.FILESET_UPLOAD_END_PROPERTY.equals(name)) {
            this.step = 1;
            processingBar.setValue(step);
            processingBar.setString(STEPS.get(step));
        } else if (StatusLabel.METADATA_IMPORTED_PROPERTY.equals(name)) {
            this.step = 2;
            processingBar.setValue(step);
            processingBar.setString(STEPS.get(step));
        } else if (StatusLabel.PIXELDATA_PROCESSED_PROPERTY.equals(name)) {
            this.step = 3;
            processingBar.setValue(step);
            processingBar.setString(STEPS.get(step));
        } else if (StatusLabel.THUMBNAILS_GENERATED_PROPERTY.equals(name)) {
            this.step = 4;
            processingBar.setValue(step);
            processingBar.setString(STEPS.get(step));
        } else if (StatusLabel.METADATA_PROCESSED_PROPERTY.equals(name)) {
            this.step = 5;
            processingBar.setValue(step);
            processingBar.setString(STEPS.get(step));
        } else if (StatusLabel.FILE_IMPORT_STARTED_PROPERTY.equals(name)) {
            Iterator<JLabel> i = labels.iterator();
            while (i.hasNext()) {
                i.next().setVisible(true);
            }
            generalLabel.setText("");
            uploadBar.setVisible(true);
            processingBar.setVisible(true);
        } else if (StatusLabel.PROCESSING_ERROR_PROPERTY.equals(name)) {
            generalLabel.setText((String) evt.getNewValue());
            if (step > 0)
                processingBar.setString(STEP_FAILURES.get(step));
        }
    }

}
