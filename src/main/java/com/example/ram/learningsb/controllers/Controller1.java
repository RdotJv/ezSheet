package com.example.ram.learningsb.controllers;

import com.example.ram.learningsb.dto.Piece;
import com.example.ram.learningsb.entities.PieceEntity;
import com.example.ram.learningsb.repositories.SheetsRepo;
import com.example.ram.learningsb.services.WsService;
import com.sun.tools.jconsole.JConsoleContext;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.*;

@CrossOrigin(origins = "http://localhost:5173/")
@RestController
public class Controller1 {
    @Autowired
    private SheetsRepo sheetsRepo;
    @Autowired
    private WsService wsService;
    @Autowired
    private EntityManagerFactory emf;
//    private PieceEntity pieceProfile = new PieceEntity();

    @GetMapping(path = "/scrape/{composerName}/{pieceName}")
    public PieceEntity scrape(@PathVariable("composerName") String composerName, @PathVariable("pieceName") String pieceName) {
        String[] pieceInfo = wsService.initialSearch(composerName, pieceName);
        pieceInfo[0] = pieceInfo[0]==null? pieceName: pieceInfo[0];
        PieceEntity pieceProfile = new PieceEntity();
        pieceProfile.setComposername(composerName.toLowerCase());
        pieceProfile.setSheetname(pieceName.toLowerCase());
        pieceProfile.setFilename(pieceInfo[0].toLowerCase());
        pieceProfile.setSheeturl(pieceInfo[1]);
//        pieceProfile.setCurrent(0);
        wsService.resultLinks.set(0,null);
        pieceProfile.setUnscrapedvariations(wsService.resultLinks);
        pieceProfile.setScrapedvariations(new ArrayList<String>(List.of(pieceProfile.getSheeturl())));
        add2Repo(pieceProfile);
        return pieceProfile;
    }

    @GetMapping(path = "/toggle/{num}")
    public String[] toggle(@PathVariable("num") Integer num, @RequestParam("pieceId") Integer pieceId) {
        System.out.println("piece id = "+pieceId + "\nnum = "+num);
        String[] pieceInfo;
        var em = emf.createEntityManager();
        em.getTransaction().begin();
        PieceEntity requestedPieceEntity = em.find(PieceEntity.class, pieceId);
//        num  = num+requestedPieceEntity.getCurrent();
        System.out.println("num"+num);
        if (num<0 || num==requestedPieceEntity.getUnscrapedvariations().size()) {
            em.close();
            return null;
        }
//        requestedPieceEntity.setCurrent(num);
        if (requestedPieceEntity.getUnscrapedvariations().get(num) == null) {
            pieceInfo = new String[]{""+pieceId ,requestedPieceEntity.getScrapedvariations().get(num)};
        } else {
            wsService.initDriver();
            pieceInfo = wsService.getPdfLink(num, requestedPieceEntity.getUnscrapedvariations());
            ArrayList<String> tempUnscrapedArr = requestedPieceEntity.getUnscrapedvariations();
            ArrayList<String> tempScrapedArr = requestedPieceEntity.getScrapedvariations();

            tempUnscrapedArr.set(num, null);
            tempScrapedArr.add(pieceInfo[1]);
            requestedPieceEntity.setUnscrapedvariations(tempUnscrapedArr);
            requestedPieceEntity.setScrapedvariations(tempScrapedArr);
        }
        pieceInfo[0] = ""+pieceId;//requestedPieceEntity.getSheetname();
        if (num == requestedPieceEntity.getUnscrapedvariations().size()-1) {
            String[] lastPieceInfo = Arrays.copyOf(pieceInfo, pieceInfo.length+1);
            lastPieceInfo[2] = null;
            pieceInfo = lastPieceInfo;
        }
        requestedPieceEntity.setSheeturl(pieceInfo[1]);
        em.merge(requestedPieceEntity);
        em.getTransaction().commit();
        em.close();
        return pieceInfo;
    }

    @GetMapping(path = "/download", produces = MediaType.APPLICATION_PDF_VALUE)//?pieceName=...&link=...
    public byte[] downloadPdf(@RequestParam(value = "pieceName", required = true) String pieceName,
                            @RequestParam(value = "link", required = true) String link ) {
        System.out.println("piecename"+pieceName+ "\nlink:"+link);
        System.out.println("downloading");
        return wsService.downloadPdf(pieceName, link);
    }

    @PostMapping(path = "/close")       //closes automatically every 10 minutes
    public void close() {
        wsService.cleanDriver();
    }

    @DeleteMapping(path = "/remove/{sheetId}")
    public void removeSheet(@PathVariable Integer sheetId) {
        var em = emf.createEntityManager();
        em.getTransaction().begin();
        em.remove(em.find(PieceEntity.class, sheetId));
        em.getTransaction().commit();
        em.close();
    }

//    @GetMapping(path = "/all")
//    public List<Piece> getAll(){
//        List<PieceEntity> peList =sheetsRepo.findAll();
//        List<Piece> pList = new ArrayList<>();
//        peList.forEach(piece -> pList.add(new Piece(piece.getSheetid(), piece.getComposername(), piece.getSheetname(),piece.getSheeturl(),piece.getFilename())));
//        return pList;
//    }
//
//    @GetMapping(path = "/all")
//    public List<PieceEntity> getAll(){
//        return sheetsRepo.findAll();
//    }

    @GetMapping(path = "/all")
    public List<PieceEntity> getAll2(@RequestParam("sheetname")String sheetname, @RequestParam("composername")String composername){
        var searchResult = sheetsRepo.findBySheetnameContainingAndComposernameContaining(sheetname.strip(), composername.strip());
        return searchResult.isEmpty() ? List.of(scrape(composername, sheetname)): searchResult;

    }

    @Transactional
    @PostMapping(path = "/edit/{sheetid}/{newPieceName}")
    public void editPieceName(@PathVariable String sheetid, @PathVariable String newPieceName) {
        var em = emf.createEntityManager();
        em.getTransaction().begin();
        var p = em.find(PieceEntity.class, sheetid);
        p.setSheetname(newPieceName);
        em.merge(p);
        em.getTransaction().commit();
        em.close();
    }

    @Transactional
//    @PostMapping(path = "/add")
    public void add2Repo(PieceEntity pieceEntity) {
        if (sheetsRepo.existsBysheetname(pieceEntity.getSheetname()) && sheetsRepo.existsBycomposername(pieceEntity.getComposername())) {
            System.out.println("piece already in database");
            return;
        }
        var em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(pieceEntity);
        em.getTransaction().commit();
        em.close();
        close();
//        sheetsRepo.save(pieceProfile); //id was not being incremented
    }
}
