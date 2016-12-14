/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestioninscription;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import sql.GestionBdd;
import java.util.*;
import javax.swing.JOptionPane;

// Imports pour le pdf
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.awt.Desktop;

import java.io.*;
import java.io.OutputStream;
import static java.lang.Integer.parseInt;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adrien
 */
public final class FenSessionFuture extends javax.swing.JFrame
{

    Connection conn;
    Statement stmt = null;
    
    /**
     * Creates new form FenSessionFuture
     */
    public FenSessionFuture()
    {
        initComponents();
        stmt = GestionBdd.connexionBdd(GestionBdd.TYPE_MYSQL, "formarmor", "localhost", "root", "");
        renseigne();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblSesFuture = new javax.swing.JTable();
        btnGenPDF = new javax.swing.JButton();

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 48)); // NOI18N
        jLabel1.setText("SESSIONS FUTURES");

        tblSesFuture.setModel(new ModeleJTableListeSession());
        jScrollPane5.setViewportView(tblSesFuture);

        btnGenPDF.setText("Générer le pdf");
        btnGenPDF.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnGenPDFActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 778, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(349, 349, 349)
                .addComponent(btnGenPDF)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(138, 138, 138))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnGenPDF)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jLabel1.getAccessibleContext().setAccessibleName("lblTitre");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGenPDFActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnGenPDFActionPerformed
    {//GEN-HEADEREND:event_btnGenPDFActionPerformed
        int nomSession = parseInt(tblSesFuture.getValueAt(tblSesFuture.getSelectedRow(), 0).toString());
        try
        {
            createPdf(nomSession);
        }
        catch (DocumentException ex)
        {
            Logger.getLogger(FenSessionFuture.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(FenSessionFuture.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnGenPDFActionPerformed

    private void createPdf(int idSession) throws DocumentException, IOException
    {
        // Génération du pdf
        String home = System.getProperty("user.home");
        OutputStream output = new FileOutputStream(home + "/Downloads/feuilleEmarg.pdf");
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, output);
        // Ouverture du document pour écrire
        document.open();
        // Ajout des metadatas
        document.addTitle("PDF de la session ");
        document.addSubject("iText");
        document.addKeywords("Sessions Futures");
        document.addAuthor("Adrien");
        document.addCreator("Adrien");
        // Création de l'entête
        Paragraph preface = new Paragraph(); 
        preface.setAlignment(Element.ALIGN_CENTER);
        // Création du tableau
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        // Variable permettant de retouver si des résultats sont retournés
        int resultat = 0;
        try
        {
            // Récupération des inscrits à la session
            int i = 0;
            String requete = "SELECT DISTINCT c.*, f.*";
            requete += "FROM client c, formation f, plan_formation pl, session_formation s, inscription i ";
            requete += "WHERE s.id=" + idSession + " ";
            requete += "AND s.formation_id=f.id ";
            requete += "AND s.id=i.session_formation_id ";
            requete += "AND i.client_id=c.id";
            ResultSet rs = stmt.executeQuery(requete);
            // Initialisation du tableau
            table.addCell("N°");
            table.addCell("Nom");
            table.addCell("Adresse");
            table.addCell("Code postal");
            table.addCell("Ville");
            table.addCell("Email");
//            table.addCell("Présent");
            table.addCell("Signature");
            float[] largeurCols = {3f, 15f, 10f, 5f, 6f, 16f, 7f};
            table.setWidths(largeurCols);
            // Choix d'une police plus petite pour le tableau
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
            Font fontCell = new Font(bf, 9, Font.NORMAL);
            if (rs.next())
            {
                resultat = 1;
            }
            while (rs.next())
            {
                if (i == 0)
                {
                    Chunk chunk = new Chunk(
                            "Formation : " + rs.getString("f.libelle") + "\n"
                            + "Description : " + rs.getString("f.description") + "\n"
                            + "Niveau : " + rs.getString("f.niveau") + "\n"
                            + "Type de la formation : " + rs.getString("f.type_form") + "\n"
                            + "Duree : " + String.valueOf(rs.getInt("f.duree")) + "\n\n"
                    );
                    preface.add(chunk);
                }
                table.addCell(new Phrase(String.valueOf(rs.getInt("c.id")), fontCell));
                table.addCell(new Phrase(rs.getString("c.nom"), fontCell));
                table.addCell(new Phrase(rs.getString("c.adresse"), fontCell));
                table.addCell(new Phrase(rs.getString("c.cp"), fontCell));
                table.addCell(new Phrase(rs.getString("c.ville"), fontCell));
                table.addCell(new Phrase(rs.getString("c.email"), fontCell));
                table.addCell("");
                i++;
            }
        } catch (SQLException ex)
        {
            Logger.getLogger(FenSessionFuture.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Ajout du contenu si la requete retournait au moins 1 résultat
        if (resultat == 1)
        {
            document.add(preface);
            document.add(table);
            document.close();
            if (Desktop.isDesktopSupported())
            {
                // no application registered for PDFs
                File myFile = new File(home + "/Downloads/feuilleEmarg.pdf");
                Desktop.getDesktop().open(myFile);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Personne n'est inscrit à cette session", "Erreur lors de la création du pdf", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private static void addMetaData(Document document)
    {
        
    }
    
    /**
     */
    
    public void renseigne()
    {
        try
        {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dateTemp = new Date();
            String date = dateFormat.format(dateTemp);
            int i = 0;
            String requete = "SELECT sf.*, f.* FROM session_formation sf, formation f WHERE sf.formation_id = f.id AND sf.date_debut >= '" + date + "' ORDER BY sf.date_debut ASC";
            ResultSet rs = stmt.executeQuery(requete);
            while (rs.next())
            {
                tblSesFuture.setValueAt(rs.getInt("sf.id"), i, 0);
                tblSesFuture.setValueAt(rs.getString("f.libelle"), i, 1);
                tblSesFuture.setValueAt(rs.getString("f.niveau"), i, 2);
                tblSesFuture.setValueAt(rs.getDate("sf.date_debut"), i, 3);
                tblSesFuture.setValueAt(rs.getInt("f.duree"), i, 4);
                tblSesFuture.setValueAt(rs.getInt("sf.nb_places"), i, 5);
                tblSesFuture.setValueAt(rs.getInt("sf.nb_inscrits"), i, 6);
                tblSesFuture.setValueAt(rs.getInt("f.coutrevient"), i, 7);
                i++;
            }
        }
        catch (SQLException se)
        {
            System.out.println("Erreur SQL1 : " + se.getMessage());
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGenPDF;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTable tblSesFuture;
    // End of variables declaration//GEN-END:variables
}
