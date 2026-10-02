
// ============================================================================
// CUANTIFICACION NUCLEAR AUTOMATICA - TIROIDES H&E
// QuPath 0.5 - 0.7
//
// Diseñado para procesar MULTIPLES IMAGENES mediante:
// Run -> Run for project
//
// CALIBRACION:
// Respeta las imagenes que ya estan calibradas.
// Las imagenes sin calibracion reciben 0.26 µm/pixel.
//
// DETECCION:
// Hematoxylin OD
// Area nuclear: 15 - 200 µm²
//
// EXPORTA:
// Un archivo .TXT por imagen con TODAS las mediciones nucleares
// ============================================================================


// ============================================================================
// 1. INFORMACION DE LA IMAGEN
// ============================================================================

def imageData = getCurrentImageData()

if (imageData == null) {
    println "ERROR: No hay ninguna imagen disponible."
    return
}

def imageName = getProjectEntry()?.getImageName()

if (imageName == null) {
    imageName = getCurrentServer().getMetadata().getName()
}

// Nombre seguro para guardar archivo
def safeName = imageName.replaceAll("[^a-zA-Z0-9._-]", "_")

println ""
println "============================================================"
println "PROCESANDO: " + imageName
println "============================================================"


// ============================================================================
// 2. CALIBRACION
// ============================================================================

def server = getCurrentServer()
def calibration = server.getPixelCalibration()

// Comprobar si la imagen ya tiene calibracion valida
if (
    calibration.hasPixelSizeMicrons() &&
    calibration.getPixelWidthMicrons() > 0 &&
    calibration.getPixelHeightMicrons() > 0
) {

    println "La imagen ya esta calibrada."
    println "Se conserva la calibracion original."

} else {

    println "La imagen no tiene calibracion."
    println "Asignando calibracion de 0.26 x 0.26 um/pixel."

    setPixelSizeMicrons(0.26, 0.26)

}

calibration = server.getPixelCalibration()

println "Pixel width  = " + calibration.getPixelWidthMicrons() + " um"
println "Pixel height = " + calibration.getPixelHeightMicrons() + " um"


// ============================================================================
// 3. CONFIGURAR IMAGEN COMO H&E
// ============================================================================

setImageType('BRIGHTFIELD_H_E')

println "Tipo de imagen: H&E Brightfield"


// ============================================================================
// 4. ELIMINAR DETECCIONES ANTERIORES
// ============================================================================

// Conserva anotaciones pero elimina detecciones anteriores
clearDetections()

println "Detecciones anteriores eliminadas"


// ============================================================================
// 5. DEFINIR REGION A ANALIZAR
// ============================================================================

// Si no existe ninguna ROI/anotacion:
// crea automaticamente una anotacion sobre TODA la imagen.
//
// Si ya existen anotaciones:
// utiliza esas anotaciones.

def annotations = getAnnotationObjects()

if (annotations.isEmpty()) {

    println "No existen ROI."
    println "Se analizara la imagen completa."

    createFullImageAnnotation(true)

} else {

    println "Se encontraron " + annotations.size() + " ROI/anotaciones."
    println "Se analizaran las ROI existentes."

    selectAnnotations()
}


// ============================================================================
// 6. DETECCION NUCLEAR
// ============================================================================

println ""
println "Iniciando deteccion nuclear..."

runPlugin(
    'qupath.imagej.detect.cells.WatershedCellDetection',
    '''
    {
        "detectionImageBrightfield": "Hematoxylin OD",

        "requestedPixelSizeMicrons": 0.26,

        "backgroundRadiusMicrons": 8.0,

        "backgroundByReconstruction": true,

        "medianRadiusMicrons": 0.0,

        "sigmaMicrons": 1.5,

        "minAreaMicrons": 15.0,

        "maxAreaMicrons": 200.0,

        "threshold": 0.08,

        "maxBackground": 2.0,

        "watershedPostProcess": true,

        "excludeDAB": false,

        "cellExpansionMicrons": 0.0,

        "includeNuclei": true,

        "smoothBoundaries": true,

        "makeMeasurements": true
    }
    '''
)


// ============================================================================
// 7. OBTENER NUCLEOS DETECTADOS
// ============================================================================

def detections = getDetectionObjects()

def nNuclei = detections.size()

println ""
println "Nucleos detectados: " + nNuclei


// ============================================================================
// 8. CALCULAR ASPECT RATIO
// ============================================================================
//
// Aspect Ratio = Max caliper / Min caliper
//
// Cercano a 1 = nucleo aproximadamente redondo
// Mayor valor = nucleo mas elongado
// ============================================================================

int aspectCalculados = 0

detections.each { obj ->

    def ml = obj.getMeasurementList()

    double maxCaliper = ml.get("Nucleus: Max caliper")
    double minCaliper = ml.get("Nucleus: Min caliper")

    if (
        !Double.isNaN(maxCaliper) &&
        !Double.isNaN(minCaliper) &&
        minCaliper > 0
    ) {

        double aspectRatio = maxCaliper / minCaliper

        ml.put(
            "Nucleus: Aspect Ratio",
            aspectRatio
        )

        aspectCalculados++
    }
}

println "Aspect Ratio calculado en: " + aspectCalculados + " nucleos"


// ============================================================================
// 9. ACTUALIZAR QUPATH
// ============================================================================

fireHierarchyUpdate()


// ============================================================================
// 10. CREAR CARPETA DE RESULTADOS
// ============================================================================

def outputDir = buildFilePath(
    PROJECT_BASE_DIR,
    "Resultados_nucleos"
)

mkdirs(outputDir)


// ============================================================================
// 11. EXPORTAR TODAS LAS MEDICIONES
// ============================================================================

def outputFile = buildFilePath(
    outputDir,
    safeName + "_nucleos.txt"
)

saveDetectionMeasurements(
    getCurrentImageData(),
    outputFile
)


// ============================================================================
// 12. MOSTRAR RESUMEN
// ============================================================================

println ""
println "============================================================"
println "ANALISIS TERMINADO"
println "============================================================"
println ""
println "Imagen: " + imageName
println "Calibracion: " + calibration.getPixelWidthMicrons() + " x " + calibration.getPixelHeightMicrons() + " um/pixel"
println "Nucleos: " + nNuclei
println "Aspect Ratio: " + aspectCalculados
println ""
println "Archivo:"
println outputFile
println ""
println "============================================================"
