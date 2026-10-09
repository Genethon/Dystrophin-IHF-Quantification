# Dystrophin-IHF-Quantification

**Status: repository preparation / work in progress.** The repository is **not yet a validated end-to-end reproducible release**. In particular, the trained Cellpose3 model, QuPath tissue pixel classifier, and final threshold provenance must be added or verified before the authors describe the package as complete.

Companion code for the immunohistofluorescence (IHF) analysis in the manuscript *Simple Western outperforms western blot and mass spectrometry for micro-dystrophin detection in AAV-treated Duchenne Muscular Dystrophy model* (Buscara et al.; manuscript under review).

## Workflow covered by the provided source code

1. **QuPath**: `Script_Segmentation_Quantification_PaperWB.groovy` selects tissue using QuPath pixel classifier `Class_Contour_GNT005` (class `coupe`). **WARNING: the source script calls `clearAllObjects()` and clears existing objects. Run on a backup project.**
2. **Cellpose3**: the QuPath BIoP Cellpose extension invokes the custom model `CP_20250221_112005_V3`, on `AF488`, `pixelSize = 0.65` µm, `diameter = 55`, `tileSize = 4096`, `cellprobThreshold = 1.2`, and `flowThreshold = 0.8`. Objects are labeled `Myofibers`.
3. **Membrane ROIs**: the Groovy code erodes each myofiber geometry via `geometry.buffer(-thickness)` and subtracts the eroded geometry from the original. The working protocol describes an **inward membrane band of five pixels (3.25 µm at 0.65 µm/pixel)**. **The precise unit used by this geometry call must still be confirmed on a test image**; the original code uses `thickness = 5`.
4. **Membrane intensity**: mean and standard deviation are measured for channel 3 (`AF594`) using QuPath `IntensityFeaturesPlugin` with `pixelSizeMicrons = 2.0`; annotation measurements are exported to `Intensity_Results`.
5. **R postprocessing**: `Intensity_processing_script_to_pdpf.R` reads tabulated QuPath measurement exports, calculates `median + 5 × mad` of selected membrane mean-intensity values (note: `mad` in base R uses a default consistency factor of 1.4826), flags intensities *strictly greater* than the threshold as positive, and calculates **PDPF = 100 × positive membrane count / total membrane count**, grouped by image.

### Annotation classes: historical naming

The original R code filters `membrane_Centro` and `membrane_Normale`; these are membrane ROIs derived from subcategories of myofibers based on nuclear location. **Nuclear position is not an analysis variable in this manuscript.** For this article, both classes should be pooled conceptually as `membrane_Myofibers`, the class produced by the supplied Groovy script. The original code files have been preserved unmodified; see `docs/RELEASE_CHECKLIST.md` for the harmonization work still required to run them together.

## Files

- `qupath/scripts/Script_Segmentation_Quantification_PaperWB.groovy` — original Groovy script, unchanged.
- `R/scripts/Intensity_processing_script_to_pdpf.R` — original R script, unchanged; contains local Windows project paths and historical class names.
- `qupath/classifiers/README.md` — instructions for depositing the required QuPath classifier.
- `cellpose3/models/README.md` — instructions for depositing the trained Cellpose model.
- `docs/RELEASE_CHECKLIST.md` — items to address before a public, citable release.
- `docs/REVIEWER_RESPONSE_DRAFT.md` — manuscript/reviewer response drafts; only claim availability after the repository is made public and complete.

## Software / reproducibility

The manuscript reports **QuPath 0.5.1** and **R 4.3.1**. The supplied Groovy script requires a compatible BIoP QuPath Cellpose extension and **Cellpose 3**, not Cellpose-SAM/Cellpose 4. Pin exact extension/Python package versions when recovered. Do not substitute a newly trained model without documenting the change.

**Scope:** The provided scripts address membrane-based PDPF quantification. The manuscript also describes whole-section normalized fluorescence intensity; its full implementation is not established from these two files and should be documented separately if needed to reproduce that analysis.

## Sharing and citation

This is a provisional package. No license has yet been selected: please confirm the Généthon/institutional decision before granting redistribution or reuse rights. When ready, make a numbered GitHub release and optionally archive it in Zenodo for a version-specific DOI. Until then, please do not cite this repository as a complete reproducible pipeline.
