package com.example.patientsportal.db.arrays

import com.example.patientsportal.entities.dbEntities.AlternativeDrug
import com.example.patientsportal.entities.dbEntities.Drug

object ArrayDrugs {

    val arrayDrugs = arrayOf(
        Drug(1, "BUDESONIDA 80 MCG/DISPARO, FORMOTEROL 4.5 MCG/DISPARO, AEROSOL x 120 DISPAROS, INHALATORIA", "2023/10/24 - 2024/10/24", "FREVIA - 80mcg /4.5mcg Oral Inhal. Susp. x 120 Dosis", "1 Disparo Via Inhalatoria Cada 12 Hs", 5432.78, 0, 3),
        Drug(2, "COLECALCIFEROL 50.000 UI/ml, SOLUCION en FRASCO de 2 ML, ORAL", "-", "STEROGYL - Bebib. Fco. Sol. x 2ml", "1 Ampolla Via Oral Enero", 8123.45, 0, 3),
        Drug(3, "CLORURO DE SODIO 3 MG/DISPARO AEROSOL X 45 DOSIS, NASAL", "2023/10/24 - 2024/01/24", "HYPERSOL - 3% Nasal Spray x 45ml", "6 Disparo Via Nasal Cada 6 Hs - (6Hs: 6) - (18Hs: 6) - (0Hs: 6) - (12Hs: 6)", 2567.89, 0, 4),
        Drug(4, "FLUTICASONA 50 MCG/DOSIS AZELASTINA CLORHIDRATO 137 MCG/DOSIS, SOLUCION NASAL en SPRAY por 120 DOSIS", "-", "INHALAN AZ - 0.5mg /0.137mg Nasal Spray X 120 Dosis", "2 Dosis Via Nasal Cada 12 Hs - (20Hs: 2) - (8Hs: 2)", 6798.12, 0, 3),
        Drug(5, "PICOSULFATO DE SODIO 10 MG, OXIDO DE MAGNESIO 3.5 G, ACIDO CITRICO 12 G, POLVO EN SOBRES, ORAL", "-", "NOVOPREP - 10mg Sobres x 2", "1 Sobre Via Oral Cada 12 Hs - (8Hs: 1) - (20Hs: 1) Antes De Las Comidas", 3214.56, 65, 4),
        Drug(6, "CLONAZEPAM 2.5 MG/ML, SOLUCION en FRASCO de 20 ml, ORAL", "2023/10/24 - 2024/08/24", "NEURYL - Gotas x 20ml", "1 Gota Via Oral Cada 24 Hs - (0Hs: 1)", 4536.78, 56, 2),
        Drug(7, "ZOLMITRIPTAN 2.5 MG, COMPRIMIDO, ORAL", "-", "ZOMIGON - 2.5mg Comp. x 3", "1 Comprimido Via Oral -", 8965.43, 34, 5),
        Drug(8, "HIERRO FUMARATO 330 MG, AC. ASCORBICO 100 MG, AC. FOLICO 7.5 MG, VITAMINA B12 1 MG, CAPSULA, ORAL", "2023/10/24 - 2024/10/24", "ANEMIDOX-FERRUM - Caps. x 50", "1 Comprimido Via Oral -", 1789.23, 0, 1),
        Drug(9, "PROGESTERONA 200 MG, CAPSULA, ORAL", "2023/10/24 - 2024/10/24", "PROGEST 200 - caps. blandas x 15", "1 Comprimido Via Oral -", 4321.98, 44, 6),
        Drug(10, "KETOROLAC 20 MG, COMPRIMIDO, ORAL", "-", "SINALGICO - 20mg Comp. x 20", "1 Comprimido Via Oral Cada 12 Hs -", 5698.34, 23, 4),
        Drug(11, "DIAZEPAM 5 MG, COMPRIMIDO, ORA", "-", "VALIUM - 5mg Comp. x 20", "1 Comprimido Via Oral", 7312.67, 55, 5),
        Drug(12, "DIOSMINA 450 MG, HESPERIDINA 50 MG, COMPRIMIDO, ORAL", "-", "DAFLON 500 - 500 mg Comp. Rec. x 30", "2 Comprimido Via Oral ", 3856.99, 25, 4),
        Drug(13, "ERGOTAMINA 1 MG, DIPIRONA 500 MG, CAFEINA 100 MG, COMPRIMIDO, ORAL", "-", "MIGRA DIOXADOL - Comp. x 50", "1 Comprimido Via Oral", 6234.12, 30, 3)
    )

    val arrayAlternativeDrugs = arrayOf(
        AlternativeDrug(1, arrayDrugs[1], "SUBICAL SOLUCION ORAL 100000UI/2ml Oral Sol. F. Amp. x 1", 1384.06, 40, 3),
        AlternativeDrug(2, arrayDrugs[4], "PICOPREP Sobres 10mg x 2", 13941.34, 50, 4),
        AlternativeDrug(3, arrayDrugs[7], "ANEMIDOX-FERRUM Caps. x 60", 11039.35, 45, 1)
    )
}