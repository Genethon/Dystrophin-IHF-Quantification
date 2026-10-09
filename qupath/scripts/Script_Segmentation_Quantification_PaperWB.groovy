    // Name for Contouring Pixel Classifier
    def nom_classifier = "Class_Contour_GNT005"

    // Name of the section "pathClass"
    def myClassSection="coupe"
    
    // Path where the CP retrained model is
    def pathModel=buildFilePath(PROJECT_BASE_DIR, 'CP_model','CP_20250221_112005_V3')
  
    // Cellpose channel Definition
    def chan="AF488"
    
    // CellPose Pixel Size
    def pixelSize=0.65
    
    // CellPose Tilesize 4096 if enough Memory in GPU
    def tileSize=4096
    
    // Required Diameter for CellPose Detection
    def diam = 55  
    
    
// ---------------------------------------------------------------------------------------------------------------------------
// ********************************* MAIN *************************************************
// ---------------------------------------------------------------------------------------------------------------------------

//-------------------------------
// CONTOURING OF THE TISSUE SLICE
//-------------------------------
        
        //Clearing all objects 
        // /!\ This will remove all objects from the image. If you need to process the segmentation and membrane creation and measurment on a selected annoation
        // /!\ please comment the whole section "Contouring of the tissue slice"
        
        clearAllObjects();
        
        // Creation of contour annotation
        createAnnotationsFromPixelClassifier(nom_classifier, 100000.0, 10000.0, "DELETE_EXISTING")
        
        //Eliminate all annotation that are not "myClassSection"
        annotationObjects.each { annotation ->
            nameAnnotation=annotation.getPathClass().toString() 
            if (nameAnnotation != myClassSection) {        
               selectObjectsByClassification(nameAnnotation)
                clearSelectedObjects()
                }
                    }
        //Select the tissu contour to keep for segmentation
     selectObjectsByClassification(myClassSection);
     
//-------------------------------
//CELLPOSE SEGMENTATION ---------
//-------------------------------    

// /!\ THIS MODEL REQUIRES CELLPOSE 3 INSTALLATION - IS NOT COMPATIBLE WITH CPSAM. IF RUN UNDER CELLPOSE 4 INSTALLATION, CPSAM or CPSAMv2 WILL BE USED 

 def cellpose = Cellpose2D.builder( pathModel )
                .pixelSize( pixelSize )                  // Resolution for detection in um
                .channels( chan ) 	               // Select detection channel(s)
        //        .preprocess( ImageOps.Filters.median(1) )                // List of preprocessing ImageOps to run on the images before exporting them
        //        .normalizePercentilesGlobal(0.1, 99.8, 10) // Convenience global percentile normalization. arguments are percentileMin, percentileMax, dowsample.
                .tileSize(tileSize)                  // If your GPU can take it, make larger tiles to process fewer of them. Useful for Omnipose
        //        .cellposeChannels(1,2)           // Overwrites the logic of this plugin with these two values. These will be sent directly to --chan and --chan2
                .cellprobThreshold(1.2)          // Threshold for the mask detection, defaults to 0.0
                .flowThreshold(0.8)              // Threshold for the flows, defaults to 0.4 
              .diameter(diam)                    // Median object diameter. Set to 0.0 for the `bact_omni` model or for automatic computation
        //        .useOmnipose()                   // Use the omnipose instead
        //        .addParameter("cluster")         // Any parameter from cellpose or omnipose not available in the builder. 
        //        .addParameter("save_flows")      // Any parameter from cellpose or omnipose not available in the builder.
        //        .addParameter("anisotropy", "3") // Any parameter from cellpose or omnipose not available in the builder.
        //        .cellExpansion(5.0)              // Approximate cells based upon nucleus expansion
        //        .cellConstrainScale(1.5)         // Constrain cell expansion using nucleus size
                .classify("Myofibers")       // PathClass to give newly created objects
        //        .measureShape()                  // Add shape measurements
        //        .measureIntensity()              // Add cell measurements (in all compartments)  
                .createAnnotations()             // Make annotations instead of detections. This ignores cellExpansion
        //        .simplify(0)                     // Simplification 1.6 by default, set to 0 to get the cellpose masks as precisely as possible
                .build()
        
        // Run detection for the selected objects
        
        def imageData = getCurrentImageData()
        def pathObjects = getSelectedObjects()
        if (pathObjects.isEmpty()) {
            Dialogs.showErrorMessage("Cellpose", "Please select a parent object!")
            return
        }
        cellpose.detectObjects(imageData, pathObjects)
        print("Cellpose segmentation accomplished")     
     
     
//------------------------------------------
//CREATION OF MEMBRANE ANNOTATIONS----------
//------------------------------------------

// This block will create an membrane annotation based on the cell segmentation by eroding the edge of the cell by a factor of "thickness" pixels

annos = getAnnotationObjects().findAll{(it.getPathClass()==getPathClass("Myofibers"))}
print(annos.size())
listToAdd = []
// Membrane thickness in µm 
thickness = 5

for(anno in annos) {
    roi = anno.ROI
    geometry = roi.getGeometry()
    geometry2 = geometry.buffer(-thickness).buffer(0) 
    roi2 = GeometryTools.geometryToROI(geometry2, roi.getImagePlane())   
    unifiedROI = RoiTools.difference(roi, roi2)
    classe=anno.getPathClass()
    unifiedObj = PathObjects.createAnnotationObject(unifiedROI, getPathClass("membrane_"+classe))
    listToAdd << unifiedObj 
}

//removeObjects(annos,true)
addObjects(listToAdd)
//resolveHierarchy()
print("Membrane Annotation Created !")
 
//------------------------------------------
//CREATING AND SAVING MEASURMENTS-----------
//------------------------------------------ 

// Creating the export folder 
// The folder is created in the Project Base Directory - Here it will be named "Intensity_Results"

 def results_path = buildFilePath(PROJECT_BASE_DIR, 'Intensity_Results')
        mkdirs(results_path)
        print('Results will be exported to :' + "/n"+ results_path)

// Measuring Intensity of Membranes 
membranes = getAnnotationObjects().findAll{(it.getPathClass()==getPathClass("membrane_Myofibers"))}
selectObjects(membranes)
// Change channel1 2 or 3 from false to true according to what channel intensity is needed to be measured
runPlugin('qupath.lib.algorithms.IntensityFeaturesPlugin', '{"pixelSizeMicrons":2.0,"region":"ROI","tileSizeMicrons":25.0,"channel1":false,"channel2":false,"channel3":true,"doMean":true,"doStdDev":true,"doMinMax":false,"doMedian":false,"doHaralick":false,"haralickMin":NaN,"haralickMax":NaN,"haralickDistance":1,"haralickBins":32}')

saveAnnotationMeasurements(results_path)



// ---------------------------------------------------------------------------------------------------------------------------
// ********************************* IMPORTS  *************************************************
// --------------------------------------------------------------------------------------------------------------------------

    import qupath.lib.objects.PathAnnotationObject
  //  import org.locationtech.jts.geom.util.LinearComponentExtracter
  //  import qupath.lib.objects.PathDetectionObject
  //  import qupath.lib.objects.PathObject
  //  import qupath.lib.regions.ImageRegion
    import qupath.ext.biop.cellpose.Cellpose2D
  //  import java.util.stream.Collectors
  //  import qupath.lib.gui.measure.ObservableMeasurementTableData
   // import qupath.lib.gui.scripting.QPEx
  //  import qupath.lib.regions.ImageRegion
   // import java.util.ArrayList
   // import java.nio.file.Files
   // import java.nio.file.Paths