# Cellpose3 — Trained model

## Model

- **Filename:** `CP_20250221_112005_V3`
- **Framework:** Cellpose 3
- **Application:** Segmentation of muscle fibres in immunofluorescence images.

## Training data

The accompanying manuscript reports 1,188 annotated cells from four images.

Training parameters, dataset provenance and validation details should be documented and verified against the original training records.

Availability and possible restrictions on sharing the original training images should be documented separately.

## Installation

The model weights are provided in this directory.

For use with the existing QuPath Groovy script, copy the model file into the `CP_model` folder of your QuPath project:

`<QuPath project>/CP_model/CP_20250221_112005_V3`

## Requirements

- Cellpose 3 with support for custom trained models
- Compatible QuPath Cellpose integration
- Configuration of inference parameters according to the original analysis workflow

