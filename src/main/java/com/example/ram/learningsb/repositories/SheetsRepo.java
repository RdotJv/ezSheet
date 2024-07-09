package com.example.ram.learningsb.repositories;

import com.example.ram.learningsb.dto.Piece;
import com.example.ram.learningsb.entities.PieceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SheetsRepo extends JpaRepository<PieceEntity, Integer> {

    public PieceEntity findPieceEntityBysheetname(String sheetname);

    public PieceEntity findPieceEntityBysheetid(int sheetid);

    public List<PieceEntity> findBySheetnameContainingAndComposernameContaining(String sheetname, String composername);

    public boolean existsBysheetname(String sheetname);

    public boolean existsBycomposername(String composername);
}
