package fr.istic.starproviderBC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by 17012154 on 21/11/17.
 */

public class FileUnziper {
    /**
     * Décompresse le fichier fileToUnzip dans le dossier destFolder
     * @param fileToUnzip
     * @param destFolder
     * @return the unzipped folder
     */
    public static File unzip(File fileToUnzip, File destFolder) {
        byte[] buffer = new byte[1024];

        try {
            // Si le dossier de destination n'existe pas, on le crée
            if (!destFolder.exists() && !destFolder.isDirectory()) {
                destFolder.mkdir();
            }

            ZipInputStream zis = new ZipInputStream(new FileInputStream(fileToUnzip));
            // Récupération du premier fichier du zip
            ZipEntry ze = zis.getNextEntry();

            // Tant qu'il y a des fichiers dans le zip d'origine
            while(ze != null){
                // On crée le fichier de destination
                String fileName = ze.getName();
                File newFile = new File(destFolder.getAbsolutePath() + File.separator + fileName);

                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            return destFolder;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
