# Release checklist

- [ ] Verify permission to publish the code, classifiers and model weights; agree on a LICENSE.
- [ ] Export `Class_Contour_GNT005` from the historical QuPath project and test importing it into QuPath 0.5.1.
- [ ] Locate and deposit the actual `CP_20250221_112005_V3` Cellpose3 weights; document the precise environment and BIoP extension version.
- [ ] Harmonize the two historical membrane ROI naming systems (`membrane_Centro` + `membrane_Normale` = pooled `membrane_Myofibers`) and check no ROI is counted twice. Keep original sources for provenance.
- [ ] Prepare a portable version of R code: avoid hard-coded local `setwd()` commands, use an input directory argument, ensure `threshold` is defined before it is used in plotting, and confirm export column names.
- [ ] Confirm whether the R threshold is estimated using only negative controls or a larger pool; document how inputs and threshold were selected. **This item is explicitly deferred.**
- [ ] Check membrane band width experimentally: the agreed method is 5 pixels (3.25 µm at 0.65 µm/pixel); establish how this maps to `geometry.buffer(-5)` on actual QuPath ROI geometry.
- [ ] Map `Intensity_Results` exported by Groovy to `InfDiam_Centro_results_Neg` read by historical R code. Include a de-identified sample `.txt` and expected `.csv`.
- [ ] Confirm code used for the whole-section MFI-based IHF measurement and its downstream normalization, as distinct from PDPF.
- [ ] Validate results against at least one analyzed image and compare expected output counts/PDPF.
- [ ] Update Data availability and rebuttal language only after making the appropriate materials public and verifying their links.
- [ ] Create an immutable GitHub release (e.g. `v1.0.0`) and, if desired, DOI via Zenodo.
