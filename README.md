# Automated Nuclear Quantification in Thyroid Cytology Using QuPath

## Overview

This Groovy script was developed for **QuPath (versions 0.5–0.7)** to perform automated nuclear detection, quantification, and morphometric analysis in thyroid cytological samples stained with Hematoxylin and Eosin (H&E).

The script is designed to process multiple cytological images within a QuPath project using the **Run for project** functionality, allowing standardized and reproducible nuclear analysis across different samples.

## Main Features

* **Automatic pixel calibration:** Preserves the original calibration of images that are already calibrated. Images without calibration are automatically assigned a pixel size of 0.26 × 0.26 µm.
* **H&E image configuration:** Automatically sets the image type to Brightfield H&E.
* **Automatic nuclear detection:** Uses the Watershed Cell Detection algorithm based on Hematoxylin Optical Density (OD).
* **Nuclear size filtering:** Detects nuclei within an area range of 15–200 µm².
* **ROI-based analysis:** Analyzes existing annotations or automatically creates a full-image annotation when no ROI is available.
* **Nuclear morphometric analysis:** Extracts nuclear measurements and calculates the Nuclear Aspect Ratio (maximum caliper / minimum caliper).
* **Automated data export:** Saves a separate TXT file containing nuclear measurements for each processed image.
* **Batch processing:** Supports automated analysis of multiple images within the same QuPath project.

## Output

For each processed cytological image, the script generates a TXT file containing the nuclear measurements obtained during detection, including the calculated Nuclear Aspect Ratio.

All output files are automatically stored in a folder named `Resultados_nucleos` within the QuPath project directory.

## Requirements

* QuPath 0.5–0.7
* H&E-stained thyroid cytological images
* A QuPath project containing the images to be analyzed

## How to Use

1. Open your QuPath project.
2. Open the Script Editor.
3. Copy and paste the `.groovy` script.
4. Select **Run → Run for project** to process all images.
5. Once processing is complete, locate the `Resultados_nucleos` folder in the QuPath project directory.

## Purpose

This script aims to facilitate standardized and reproducible nuclear morphometric quantification in thyroid cytological specimens. It is intended to support digital cytopathology research by reducing manual workload and enabling systematic evaluation of nuclear morphological features.

## Calibration

The script checks the calibration of each image individually:

* If the image already has a valid pixel calibration, the original calibration is preserved.
* If the image does not have a valid calibration, the script assigns **0.26 × 0.26 µm/pixel**.

This allows images with different acquisition settings or existing calibration information to be processed without unnecessarily overwriting their original calibration.

## Nuclear Measurements

The script uses the **Hematoxylin Optical Density** channel for nuclear detection and performs measurements on the detected nuclei.

The **Nuclear Aspect Ratio** is calculated as:

`Maximum Caliper / Minimum Caliper`

Values closer to 1 indicate a more approximately circular nuclear shape, while higher values indicate greater elongation.

## File Structure

A recommended repository structure is:

```text
qupath-thyroid-cytology-nuclear-quantification/
│
├── README.md
│
└── thyroid_cytology_nuclear_quantification.groovy
```

## Important Note

Detection performance may vary depending on image quality, staining intensity, cellularity, sample preparation, and image acquisition settings.

The results should be visually inspected and validated before being used for research conclusions.

## License

This project is intended for research and educational purposes. Please refer to the repository license for terms regarding use, modification, and distribution.
