# Consulta del Estado Tributario SRI | Java
La funcionalidad del aplicativo es el consultar de una lista de RUC los estados tributarios y permisos de facturacion de cada uno de los RUCS a consultar.

## Scripts:
|Nombre                                     |Descripcion |
|-------------------------------------------|------------|
|analyzerProfileRF-Image.py                 |Este script analiza perfiles de los rostros a capturar de las personas que deseamos almacenar para proceder a reconocer. |
|analyzerProfileRF-Video.py                 |Este script entrenamos el reconocedor de rostros con los siguientes: EigenFaces, FisherFaces y LBPHFaces. |
|detectProfileRF-Video.py                   |Este script ejecuta el reconocimiento facial de videos. |
|detectProfileRF-MultiImageWithFrame.py     |Este script ejecuta el reconocimiento facial de imagenes con multiples rostros y usando un frame para visualizar resultado, y genera un json con los nombres de los rostros detectados. |
|detectProfileRF-MultiImageWithoutFrame.py  |Este script ejecuta el reconocimiento facial de imagenes con multiples rostros, y genera un json con los nombres de los rostros detectados. |
|test.py                                    |Este script se realizaba para pruebas entre videos e imagenes
|trainingMethodRF.py                        |Este script entrena un modelo de OpenCV (EigenFaces, FisherFaces y LBPHFaces).
