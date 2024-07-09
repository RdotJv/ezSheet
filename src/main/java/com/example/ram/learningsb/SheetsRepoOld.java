package com.example.ram.learningsb;

import com.example.ram.learningsb.dto.Piece;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SheetsRepoOld {
    @Autowired
    public JdbcTemplate jdbcTemplate;


    public List<HashMap<String,HashMap<String,String>>> getAll() {
        String sql = "SELECT * FROM sheet_data";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
                return new HashMap<String, HashMap<String, String>>(
                            Map.of(String.valueOf(rs.getInt("sheetId")), new HashMap<String, String>(Map.of(
                                    "composerName", rs.getString("composerName"),
                                    "sheetName", rs.getString("sheetName"),
                                    "sheetUrl", rs.getString("sheetUrl")))));
                }
        );
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void addPiece(Piece piece){
        String sql = "INSERT INTO sheet_data VALUES (NULL, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,  piece.getComposerName(), piece.getSheetName(), piece.getSheetUrl(), piece.getFileName());
        System.out.println("successfully added " + piece);
    }
}
